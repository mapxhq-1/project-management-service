
package com.example.ProjectManagement.dto.NotesDto;

import com.example.ProjectManagement.model.HistoricalYear;
import lombok.Data;

@Data
public class UpdateNoteRequest {
    private String  htmlText;                // Required
    private HistoricalYear yearInTimeline;  // Required
}
