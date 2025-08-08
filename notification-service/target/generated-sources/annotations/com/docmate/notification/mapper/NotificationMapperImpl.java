package com.docmate.notification.mapper;

import com.docmate.common.entity.Notification;
import com.docmate.common.entity.User;
import com.docmate.notification.dto.NotificationDto;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:54+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto toDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDto.NotificationDtoBuilder notificationDto = NotificationDto.builder();

        notificationDto.userId( notificationUserId( notification ) );
        notificationDto.id( notification.getId() );
        notificationDto.title( notification.getTitle() );
        notificationDto.message( notification.getMessage() );
        notificationDto.type( notification.getType() );
        notificationDto.isRead( notification.getIsRead() );
        Map<String, Object> map = notification.getData();
        if ( map != null ) {
            notificationDto.data( new LinkedHashMap<String, Object>( map ) );
        }
        notificationDto.createdDate( notification.getCreatedDate() );

        return notificationDto.build();
    }

    @Override
    public Notification toEntity(NotificationDto notificationDto) {
        if ( notificationDto == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.title( notificationDto.getTitle() );
        notification.message( notificationDto.getMessage() );
        notification.type( notificationDto.getType() );
        notification.isRead( notificationDto.getIsRead() );
        Map<String, Object> map = notificationDto.getData();
        if ( map != null ) {
            notification.data( new LinkedHashMap<String, Object>( map ) );
        }

        return notification.build();
    }

    private UUID notificationUserId(Notification notification) {
        if ( notification == null ) {
            return null;
        }
        User user = notification.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
