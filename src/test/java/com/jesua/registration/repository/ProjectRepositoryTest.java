package com.jesua.registration.repository;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Project;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.ProjectBuilder.createProjectSpecification;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    private Project project;

    @BeforeAll
    void setUp(){
        ProjectDto projectDto = buildProjectDto();
        projectDto.setShortName("testName");
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);
    }

    @AfterAll
    void tearDown(){
        projectRepository.deleteById(project.getId());
    }

    @Test
    void findAll() {

        ProjectSpecification projectSpecification = createProjectSpecification("testName");
        List<Project> projects = projectRepository.findAll(projectSpecification);

        assertThat(projects.size()).isEqualTo(1);
    }

}