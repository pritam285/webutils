package com.yukthi.webutils.notification;

import java.util.Collection;
import java.util.List;

import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.annotations.SearchResult;
import com.yukthi.webutils.common.models.notification.NotificationModel;
import com.yukthi.webutils.repository.IWebutilsRepository;

/**
 * Repository for user notifications.
 * @author akiran
 */
public interface IUserNotificationRepository extends IWebutilsRepository<UserNotificationEntity>
{
	/**
	 * Used to set user preference for user notification.
	 * @param notificationType Type to set
	 * @param userId User to set
	 * @param enabled Enabled/disabled flag.
	 * @return True if able to update
	 */
	public boolean updateUserPreference(@Condition("notificationType") String notificationType, @Condition("user.id") Long userId, @Field("enabled") boolean enabled);
	
	/**
	 * Fetches the users (among specified user ids) for specified notification type. 
	 * @param notificationType notification type for which users need to be filtered.
	 * @param userIds Users who needs to be filtered.
	 * @return Matching filter ids.
	 */
	@SearchResult
	public List<NotificationSettingResult> fetchFilterUsers(@Condition("notificationType") String notificationType, 
			@Condition(value = "user.id", op = Operator.IN) Collection<Long> userIds);
	
	/**
	 * Fetches notifications for specified user.
	 * @param userId User for whom notifications has to be fetched.
	 * @return Matching notification settings.
	 */
	@SearchResult
	public List<NotificationModel> fetchNotificationsForUser(@Condition("user.id") Long userId); 
}
