﻿namespace Publisher.DTO.RequestDTO;

public class NewsRequestDTO
{
    public long Id { get; set; }
    public string Title { get; set; }
    
    public long AuthorId { get; set; }

    public string Content { get; set; }
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

}