package com.chatApplication.repository;


import com.chatApplication.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {


    @Query("SELECT cm FROM ChatMessage cm WHERE cm.type='PRIVATE_MESSAGE' AND" +
            "((cm.sender=:user1 AND cm.recipient=:user2) OR (cm.sender=:user2 AND cm.recipient=:user1))"+
            "ORDER BY cm.timeStamp ASC")
    List<ChatMessage> findPrivateMessageBetweenTwoUsers(@Param("user1") String user1,@Param("user2") String user2);
}
