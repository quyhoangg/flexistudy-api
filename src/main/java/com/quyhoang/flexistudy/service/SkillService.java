package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.entity.Skill;
import com.quyhoang.flexistudy.repository.SkillRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillService {
    SkillRepository skillRepository;

    public List<String> suggestSkills(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        return skillRepository.searchByKeyword(keyword.trim())
                .stream()
                .map(Skill::getName)
                .toList();
    }
}
