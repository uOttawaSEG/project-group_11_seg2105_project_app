package com.example.group_11_project_app_seg2105.data;

import java.util.List;


public interface AvailabilityRepository {

    
    boolean create(String tutorEmail, String date, String start, String end);

    
    List<AvailabilitySlot> findByTutorAndDate(String tutorEmail, String date);

    
    List<AvailabilitySlot> findByTutor(String tutorEmail);

    
    boolean delete(AvailabilitySlot slot);
}