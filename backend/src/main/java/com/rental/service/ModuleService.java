package com.rental.service;

import com.rental.dto.request.ModuleRequest;
import com.rental.dto.response.ModuleResponse;
import com.rental.entity.Module;
import com.rental.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Transactional(readOnly = true)
    public List<ModuleResponse> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::toModuleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ModuleResponse createModule(ModuleRequest request) {
        Module module = new Module();
        module.setModuleName(request.getModuleName());
        module.setModuleKey(request.getModuleKey());
        module.setDescription(request.getDescription());
        
        Module savedModule = moduleRepository.save(module);
        return toModuleResponse(savedModule);
    }

    @Transactional
    public ModuleResponse updateModule(Integer id, ModuleRequest request) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        
        module.setModuleName(request.getModuleName());
        module.setModuleKey(request.getModuleKey());
        module.setDescription(request.getDescription());
        
        Module updatedModule = moduleRepository.save(module);
        return toModuleResponse(updatedModule);
    }

    @Transactional
    public void deleteModule(Integer id) {
        moduleRepository.deleteById(id);
    }

    private ModuleResponse toModuleResponse(Module module) {
        return new ModuleResponse(
                module.getModuleId(),
                module.getModuleName(),
                module.getModuleKey(),
                module.getDescription()
        );
    }
}
