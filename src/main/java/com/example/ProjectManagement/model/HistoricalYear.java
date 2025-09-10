package com.example.ProjectManagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HistoricalYear {
    private long year;
    private String era;

    @Override
    public String toString() {
        return year + " " + era;
    }
}

