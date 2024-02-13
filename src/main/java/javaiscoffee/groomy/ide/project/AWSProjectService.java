package javaiscoffee.groomy.ide.project;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import com.amazonaws.services.ecs.model.ContainerDefinition;
import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSProjectService {

    /**
     * 프로젝트 컨테이너 생성 및 실행
     */
    public boolean launchProjectEnvironment(Long memberId, Long projectId, ProjectLanguage language) {
        // 1. ECS Task Definition 생성
        AmazonECS ecsClient = AmazonECSClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2) // 원하는 AWS 리전 설정
                .build();
        ContainerDefinition containerDefinition = new ContainerDefinition()
                .withName(String.format("member-%d-project-%d-container", memberId, projectId)) //컨테이너 이름 지정
                .withImage(language.getImage()) // 사용할 Docker 이미지
                .withCpu(512)
                .withMemory(1024)
                .withEssential(true)
                .withEnvironment(new KeyValuePair().withName("ENV_VAR_NAME").withValue("value")); // 환경 변수 설정

        RegisterTaskDefinitionRequest request = new RegisterTaskDefinitionRequest()
                .withFamily("project-environment-creator") // 태스크 정의의 이름
                .withRequiresCompatibilities("EC2") // 호환성 설정 (FARGATE 또는 EC2)
                .withCpu("256") // 태스크 레벨의 CPU 요구 사항
                .withMemory("512") // 태스크 레벨의 메모리 요구 사항
                .withNetworkMode("awsvpc") // 네트워크 모드 설정
                .withExecutionRoleArn("arn:aws:iam::account-id:role/ecsTaskExecutionRole") // 실행 역할 ARN
                .withContainerDefinitions(containerDefinition); // 컨테이너 정의 추가

        RegisterTaskDefinitionResult response = ecsClient.registerTaskDefinition(request);

        // 2. ECS 컨테이너 실행

        // ECS 클러스터 이름 설정
        String clusterName = "yourClusterName";
        // 언어에 따라 태스크 종류 선택
        String taskDefinition = getTaskDefinitionByLanguage(language);
        // ECS Task 실행
        RunTaskRequest runTaskRequest = new RunTaskRequest()
                .withCluster("groomy-cluster")
                .withTaskDefinition(taskDefinition)
                .withCount(1)
                .withLaunchType(LaunchType.FARGATE) // 또는 "EC2"
                .withOverrides(new TaskOverride()
                        .withContainerOverrides(new ContainerOverride()
                                .withName("project/"+memberId+"-"+projectId)
                                .withEnvironment(new KeyValuePair()
                                        .withName("PROJECT_PATH")
                                        .withValue("/home/projects/" + memberId + "/" + projectId))));

        RunTaskResult runTaskResult = ecsClient.runTask(runTaskRequest);

        // 3. 프로젝트 환경 설정 확인 및 테스트 (실제 구현에서 필요에 따라 추가)

        // 4. 사용자에게 접근 정보 제공 (실제 구현에서 필요에 따라 추가)

        return true;
    }
    private String getTaskDefinitionByLanguage(ProjectLanguage language) {
        return switch (language) {
            case JAVA -> "java-task-definition:1";
            case JAVASCRIPT -> "javascript-task-definition:1";
            case PYTHON -> "python-task-definition:1";
            default -> "default-task-definition:1";
        };
    }
}
