package com.example.group_11_project_app_seg2105.data;

import java.util.List;

public interface RegistrationRequestRepository {
    RegistrationRequest create(String email, RegistrationStatus status, String reason);
    RegistrationRequest findById(long id);
    List<RegistrationRequest> findAll();
    List<RegistrationRequest> findByStatus(RegistrationStatus status);
    boolean updateStatus(long id, RegistrationStatus status, String decidedBy, String reason);
    boolean delete(long id);
}
