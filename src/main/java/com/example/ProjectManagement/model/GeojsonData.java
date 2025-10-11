package com.example.ProjectManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeojsonData {
    private TreeMap<String, Object> content; // Accepts any JSON structur
}
