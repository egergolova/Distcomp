package com.homel.user_stories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseTo {
    private Long id;
    private String state;
    private Long storyId;
    private String content;
}
