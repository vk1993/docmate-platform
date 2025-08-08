package com.docmate.notification.mapper;

import com.docmate.common.entity.Notification;
import com.docmate.notification.dto.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    
    @Mapping(target = "userId", source = "user.id")
    NotificationDto toDto(Notification notification);
    
    @Mapping(target = "user", ignore = true)
    Notification toEntity(NotificationDto notificationDto);
}
