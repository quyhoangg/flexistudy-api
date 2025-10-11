package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.AvailabilityWindowCreateRequest;
import com.quyhoang.flexistudy.dto.request.AvailabilityWindowUpdateRequest;
import com.quyhoang.flexistudy.entity.AvailabilityWindow;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.repository.AvailabilityWindowRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AvailabilityWindowService {
    AvailabilityWindowRepository availabilityWindowRepository;
    UserRepository userRepository;

    @Transactional
    public AvailabilityWindow create(AvailabilityWindowCreateRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        AvailabilityWindow window = AvailabilityWindow.builder()
                .user(user)
                .dayOfWeek(req.getDayOfWeek())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .note(req.getNote())
                .build();

        return availabilityWindowRepository.save(window);
    }

    @Transactional
    public AvailabilityWindow update(String id, AvailabilityWindowUpdateRequest req) {
        AvailabilityWindow window = availabilityWindowRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AVAILABILITY_NOT_FOUND));

        window.setStartTime(req.getStartTime());
        window.setEndTime(req.getEndTime());
        window.setNote(req.getNote());

        return availabilityWindowRepository.save(window);
    }

    @Transactional
    public void delete(String id) {
        if (!availabilityWindowRepository.existsById(id))
            throw new AppException(ErrorCode.AVAILABILITY_NOT_FOUND);
        availabilityWindowRepository.deleteById(id);
    }

    public AvailabilityWindow getById(String id) {
        return availabilityWindowRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AVAILABILITY_NOT_FOUND));
    }

    public List<AvailabilityWindow> getByUser(String userId) {
        return availabilityWindowRepository.findByUser_Id(userId);
    }
}
