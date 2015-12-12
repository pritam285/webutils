import com.yukthi.utils.annotations.IgnorePropertyDestination;
	/**
	 * Id of the corresponding file entity
	 */
	/**
	 * Version of the corresponding file entity
	 */
	@IgnorePropertyDestination
	/**
	 * Size of the file in mb
	 */
	
	/**
	 * Gets the id of the corresponding file entity.
	 *
	 * @return the id of the corresponding file entity
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the corresponding file entity.
	 *
	 * @param id the new id of the corresponding file entity
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the version of the corresponding file entity.
	 *
	 * @return the version of the corresponding file entity
	 */
	public Integer getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of the corresponding file entity.
	 *
	 * @param version the new version of the corresponding file entity
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}

	/**
	 * Gets the size of the file in mb.
	 *
	 * @return the size of the file in mb
	 */
	public long getSizeInMb()
	{
		return sizeInMb;
	}

	/**
	 * Sets the size of the file in mb.
	 *
	 * @param sizeInMb the new size of the file in mb
	 */
	public void setSizeInMb(long sizeInMb)
	{
		this.sizeInMb = sizeInMb;
	}