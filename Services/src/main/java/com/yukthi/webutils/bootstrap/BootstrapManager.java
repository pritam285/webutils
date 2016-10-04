package com.yukthi.webutils.bootstrap;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.UniqueConstraintViolationException;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.search.SearchCondition;
import com.yukthi.persistence.repository.search.SearchQuery;
import com.yukthi.utils.ReflectionUtils;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.repository.UserEntity;
import com.yukthi.webutils.repository.WebutilsEntity;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.security.UserDetails;
import com.yukthi.webutils.services.CurrentUserService;
import com.yukthi.webutils.services.UserService;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Bootstrap manage to load bootstrap data into db.
 * @author akiran
 */
@Service
public class BootstrapManager
{
	private static Logger logger = LogManager.getLogger(BootstrapManager.class);
	
	private static class ServiceMethod
	{
		/**
		 * Service class on which method needs to be invoked.
		 */
		private Object service;
		
		/**
		 * Service method to be invoked for save.
		 */
		private Method method;

		/**
		 * Instantiates a new service method.
		 *
		 * @param service the service
		 * @param method the method
		 */
		public ServiceMethod(Object service, Method method)
		{
			this.service = service;
			this.method = method;
		}
		
		/**
		 * Invokes the service method with specified model.
		 * @param model Model to save
		 */
		public void invoke(Object model) throws Exception
		{
			try
			{
				method.invoke(service, model);
			} catch(InvocationTargetException ex)
			{
				throw (Exception) ex.getCause();
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while invoking saving model using service method - {}", model);
			}
		}
	}

	/**
	 * Pattern for expressions in bootstrap values.
	 */
	private static final Pattern EXPR_PATTERN = Pattern.compile("\\$\\$?\\{([\\w\\.\\(\\)\\'\\ ]+)\\}");
	
	/**
	 * Service mthos pattern.
	 */
	private static final Pattern SERVICE_METHOD_PATTERN = Pattern.compile("([\\w\\.]+)\\.(\\w+)\\(([\\w\\.]+)\\)");

	/**
	 * Class name index.
	 */
	private static final int IDX_CLASS_NAME = 1;
	
	/**
	 * Method name index.
	 */
	private static final int IDX_METHOD_NAME = 2;
	
	/**
	 * Param type index.
	 */
	private static final int IDX_PARAM_TYPE = 3;

	/**
	 * File bootstrap data can be found.
	 */
	@Value("${app.bootstrap.files}")
	private String bootstrapDataFile;

	/**
	 * Repository factories to save and fetch entities.
	 */
	@Autowired
	private RepositoryFactory repositoryFactory;

	/**
	 * Context to keep track of loaded beans.
	 */
	private BootstrapLoadContext context = new BootstrapLoadContext();

	/**
	 * Object mapper to parse json data.
	 */
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * User service to fetch default users.
	 */
	@Autowired
	private UserService userService;
	
	/**
	 * Application context used to fetch service instances.
	 */
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Current user service to manage default active user.
	 */
	@Autowired
	private CurrentUserService currentUserService;
	
	/**
	 * Security service to build user details from user entity.
	 */
	@Autowired
	private ISecurityService securityService;

	/**
	 * Setter for setting bootstrap file.
	 * @param bootstrapDataFile File to load.
	 */
	public void setBootstrapDataFile(String bootstrapDataFile)
	{
		this.bootstrapDataFile = bootstrapDataFile;
	}

	/**
	 * Replaces expressions in given string.
	 * @param str String to parse.
	 * @return Parsed string after replacing expressions.
	 */
	private String replaceExpressions(String str) throws Exception
	{
		Matcher matcher = EXPR_PATTERN.matcher(str);

		StringBuffer buffer = new StringBuffer();

		while(matcher.find())
		{
			if(matcher.group().startsWith("$$"))
			{
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(matcher.group().substring(1))) ;
				continue;
			}
			
			matcher.appendReplacement(buffer, "" + PropertyUtils.getProperty(context, matcher.group(1)));
		}

		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/**
	 * Parse service method string into object that can be used for invocation of save method. 
	 * @param modelType Model type for which service method was specified.
	 * @param serviceMethod Service method string.
	 * @return Parsed service method object.
	 */
	private ServiceMethod parseServiceMethod(Class<?> modelType, String serviceMethod)
	{
		Matcher matcher = SERVICE_METHOD_PATTERN.matcher(serviceMethod);
		
		if(!matcher.matches())
		{
			throw new InvalidArgumentException("Invalid service method format encountered - '{}'. Expected format - <class-name>.<method-name>(param-type)", serviceMethod);
		}
		
		Class<?> serviceClass = null;
		
		try
		{
			serviceClass = Class.forName(matcher.group(IDX_CLASS_NAME));
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid service class name specified - '{}' in service method string - {}", matcher.group(IDX_CLASS_NAME), serviceMethod);
		}
		
		Object service = applicationContext.getBean(serviceClass);
		
		if(service == null)
		{
			throw new InvalidStateException("No service registered with spring of type - {}", matcher.group(IDX_CLASS_NAME));
		}
		
		Class<?> paramType = null;
		
		try
		{
			paramType = Class.forName(matcher.group(IDX_PARAM_TYPE));
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid param type specified - '{}' in service method string - {}", matcher.group(IDX_PARAM_TYPE), serviceMethod);
		}
		
		if(!modelType.equals(paramType))
		{
			throw new InvalidArgumentException("Model type {} is not matching specified service method argument type - {}", modelType.getName(), paramType.getName());
		}
		
		Method method = null;
		
		try
		{
			method = serviceClass.getMethod(matcher.group(IDX_METHOD_NAME), paramType);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("No method found with specified signature - {}", serviceMethod);
		}
		
		return new ServiceMethod(service, method);
	}
	
	/**
	 * Fetches the entity from specified repository using specified id field.
	 * @param repository Repository to use for fetching.
	 * @param model Model to use for id field value.
	 * @param idField Id field name.
	 * @param entityType Entity type expected to fetch.
	 * @return Resulted entity object.
	 */
	private Object fetchByIdField(ICrudRepository<Object> repository, Object model, String idField, Class<?> entityType)
	{
		SearchQuery searchQuery = new SearchQuery(
				new SearchCondition(idField, Operator.EQ, ReflectionUtils.getFieldValue(model, idField))
		);
			
		List<Object> resLst = repository.search(searchQuery);
		
		if(CollectionUtils.isNotEmpty(resLst))
		{
			return resLst.get(0);
		}
		else
		{
			throw new InvalidStateException(
					"An unique constraint exception occurred while saving entity {}. Fetch also failed with identity field '{}' with value - {}", 
					entityType.getName(), idField, ReflectionUtils.getFieldValue(model, idField));
		}
	}
	
	/**
	 * Saves the specified model object using specified service method.
	 * @param model Model to be saved.
	 * @param serviceMethod Service method to be used for saving.
	 * @param repository Repository to be used for fetching saved object.
	 * @param idField Id field name to be used for fetching.
	 * @param entityType Expected entity type.
	 * @return Saved entity object if any.
	 */
	private Object saveByServiceMethod(Object model, ServiceMethod serviceMethod, ICrudRepository<Object> repository, String idField, Class<?> entityType) throws Exception
	{
		serviceMethod.invoke(model);
		
		return fetchByIdField(repository, model, idField, entityType);
	}
	
	/**
	 * Saves the model with specified repository.
	 * @param model Model to be saved.
	 * @param repository Repository to be used for saving.
	 * @param entityType Entity type expected to be fetched.
	 * @param spaceIdentity Space identity to be set on entity.
	 * @param defaultUser Default user to be used for tracking.
	 * @return Saved entity.
	 */
	private Object saveByRepository(Object model, ICrudRepository<Object> repository, Class<?> entityType, String spaceIdentity, UserEntity defaultUser)
	{
		Object entity = WebUtils.convertBean(model, entityType);
		
		//set default fields
		if(entity instanceof WebutilsEntity)
		{
			WebutilsEntity webutilsEntity = (WebutilsEntity) entity;
			
			webutilsEntity.setVersion(1);
			webutilsEntity.setSpaceIdentity(spaceIdentity);

			//set date fields
			Date now = new Date();
			webutilsEntity.setCreatedOn(now);
			webutilsEntity.setUpdatedOn(now);

			//set user fields
			if(webutilsEntity.getCreatedBy() == null && defaultUser != null)
			{
				webutilsEntity.setCreatedBy(defaultUser);
				webutilsEntity.setUpdatedBy(defaultUser);
			}
		}
		
		boolean res = repository.save(entity);
		
		if(!res)
		{
			throw new InvalidStateException("Failed to save entity by repository - {}", entity);
		}
		
		return entity;
	}

	/**
	 * Loads entity groups and saves entities specified in it.
	 * @param entityGroup Entity group to load.
	 * @param defaultUserName Default user to be used for tracking fields.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadEntityGroup(BootstrapData.EntityGroup entityGroup, String defaultUserName) throws Exception
	{
		logger.debug("Loading entity group - {} with indentity field - {}. Default user - {}", entityGroup.getName(), entityGroup.getIdentityField(), defaultUserName);

		Class<?> entityType = Class.forName(entityGroup.getEntityType());
		Class<?> modelType = Class.forName(entityGroup.getModelType());
		Object entity = null, model = null;
		String entityJson = null;
		String identity = null;
		String identityField = entityGroup.getIdentityField();

		ICrudRepository<Object> crudRepository = (ICrudRepository) repositoryFactory.getRepositoryForEntity(entityType);
		
		UserEntity defaultUser = null;
		
		//if default user is specified, fetch it and set it as active user
		if(defaultUserName != null)
		{
			defaultUser = userService.getUser(defaultUserName, null);
			
			if(defaultUser == null)
			{
				throw new InvalidStateException("Failed to fetch default user with specified name - {}", defaultUserName);
			}
			
			UserDetails userDetails = securityService.getUserDetailsFor(defaultUser);
			currentUserService.setInternalCurrentUser(userDetails);
		}
		
		String serviceMethodStr = entityGroup.getServiceMethod();
		ServiceMethod serviceMethod = null;
		
		if(serviceMethodStr != null)
		{
			serviceMethod = parseServiceMethod(modelType, serviceMethodStr);
		}
		
		for(Map<String, Object> entityMap : entityGroup.getEntities())
		{
			logger.debug("Saving entity {} via model {} with properties - {}", entityType.getName(), modelType.getName(), entityMap);

			identity = (String) entityMap.remove("#identity");

			entityJson = objectMapper.writeValueAsString(entityMap);
			entityJson = replaceExpressions(entityJson);

			logger.debug("Saving with model json - {}", entityJson);

			// create the entity and populate the properties
			model = objectMapper.readValue(entityJson, modelType);

			try
			{
				if(serviceMethod != null)
				{
					if(identityField == null)
					{
						throw new InvalidArgumentException("No indentity field specified for entity group '{}'. Identity field is mandatory for service method based groups.", entityGroup.getName());
					}
					
					entity = saveByServiceMethod(model, serviceMethod, crudRepository, identityField, entityType);
				}
				else
				{
					entity = saveByRepository(model, crudRepository, entityType, entityGroup.getSpaceIdentity(), defaultUser);
				}

				logger.debug("Saved entity {} successfully", entity);
			} catch(UniqueConstraintViolationException ex)
			{
				if(identityField != null)
				{
					SearchQuery searchQuery = new SearchQuery(
						new SearchCondition(identityField, Operator.EQ, ReflectionUtils.getFieldValue(model, identityField))
					);
					
					List<Object> resLst = crudRepository.search(searchQuery);
					
					if(CollectionUtils.isNotEmpty(resLst))
					{
						entity = resLst.get(0);
					}
					else
					{
						throw new InvalidStateException(
								"An unique constraint exception occurred while saving entity {}. Fetch also failed with identity field '{}' with value - {}", 
								entity, identityField, ReflectionUtils.getFieldValue(entity, identityField));
					}
				}
				
				logger.warn("An unique constrain exception occurred while saving entity. Assuming entity already exist. Entity: {} \n\tError: {}", entity, "" + ex);
			}

			//if no direct identity is specified
			if(identity == null)
			{
				//check if any identity field is specified on entity and use it
				if(entityGroup.getIdentityField() != null)
				{
					context.addedEntity(entityGroup.getName(), "" + PropertyUtils.getProperty(entity, entityGroup.getIdentityField()), entity);
				}
			}
			//if static id value is specified, use it directly
			else
			{
				context.addedEntity(entityGroup.getName(), "" + identity, entity);
			}
		}
		
		//reset default user, in case it is set
		currentUserService.setInternalCurrentUser(null);
	}

	/**
	 * Loads the specified bootstrap file.
	 * @param bootstrapDataFile Bootstrap file to load
	 * @return true if bootstrap file is loaded.
	 */
	private boolean loadBootstrapData(String bootstrapDataFile) throws Exception
	{
		File dataFile = new File(bootstrapDataFile);

		if(!dataFile.exists())
		{
			logger.warn("Configured bootstrap data file does not exist - " + bootstrapDataFile);
			return false;
		}

		// check if the data file is modified from last load
		File loadedFile = new File(bootstrapDataFile + ".loaded");

		if(loadedFile.exists())
		{
			logger.warn("Found bootstrap file '{}' is already loaded. Hence skipping data load.", bootstrapDataFile);
			return false;
		}

		// load the data file
		logger.debug("Loading data file - {}", bootstrapDataFile);

		ObjectMapper objectMapper = new ObjectMapper();
		BootstrapData bootstrapData = objectMapper.readValue(dataFile, BootstrapData.class);

		for(BootstrapData.EntityGroup entityGroup : bootstrapData.getEntityGroups())
		{
			loadEntityGroup(entityGroup, bootstrapData.getDefaultUserName());
		}

		if(!loadedFile.exists())
		{
			loadedFile.createNewFile();
		}

		return true;
	}

	/**
	 * Post construct method to load bootstrap file.
	 */
	public void load()
	{
		// check if the data file is configured and available
		if(StringUtils.isBlank(bootstrapDataFile))
		{
			logger.debug("No bootstrap file(s) configured. Skipping bootstrap data load.");
			return;
		}
		
		String files[] = bootstrapDataFile.split("\\s*\\,\\s*");
		
		logger.debug("Loading bootstrap files - {}", Arrays.toString(files));

		for(String file : files)
		{
			try
			{
				loadBootstrapData(file);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while loading the bootstrap files", ex);
			}
		}

		logger.debug("Bootstrap data loaded successfully..");
	}
}