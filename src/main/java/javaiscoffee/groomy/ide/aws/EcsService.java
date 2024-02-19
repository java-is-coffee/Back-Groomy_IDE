package javaiscoffee.groomy.ide.aws;

import javaiscoffee.groomy.ide.project.ProjectLanguage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcsService {
    //클러스터 이름
    private static final String CLUSTER_NAME = "Groomy-cluster";
    //태스크 정의 그룹
    private static final String TASK_DEFINITION_FAMILY = "groomy-user-project";
    //ecs 클라이언트
    private static EcsClient ecsClient = EcsClient.builder().region(Region.AP_NORTHEAST_2).build();


    /**
     * 태스크를 처음 정의하고 실행시키는 메서드
     * 프로젝트 생성할 때 사용
     */
    public RunTaskResponse createAndRunTask(Long memberId, Long projectId, ProjectLanguage language) {
        log.info("태스크 실행 시작");
        Volume volume = createVolume(memberId, projectId);
        ContainerDefinition containerDefinition = createContainerDefinition(selectContainerImageByLanguage(language), createLogConfiguration());
        RegisterTaskDefinitionResponse registerTaskDefinitionResponse = registerTaskDefinition(volume, containerDefinition);
        RunTaskResponse runTaskResponse = runTask(registerTaskDefinitionResponse.taskDefinition().taskDefinitionArn());
        log.info("태스크 실행 결과 = {}",runTaskResponse.toString());

        List<Task> tasks = runTaskResponse.tasks();
        for (Task task : tasks) {
            String taskArn =  task.taskArn();
        }
        return  runTaskResponse;
    }

    /**
     * 태스크 컨테이너를 정지시키는 메서드
     */
    public void stopTask(String taskArn) {
        log.info("태스크 정지 시작: {}", taskArn);
        ecsClient.stopTask(StopTaskRequest.builder()
                        .cluster(CLUSTER_NAME)
                        .task(taskArn)
                        .reason("No Using Member")
                        .build());
    }

    /**
     * 태스크 정의를 이용해 컨테이너를 실행하고 응답 저장
     */
    public RunTaskResponse runTask(String taskDefinitionArn) {
        return ecsClient.runTask(RunTaskRequest.builder()
                .cluster(CLUSTER_NAME)
                .taskDefinition(taskDefinitionArn)
                .networkConfiguration(networkConfiguration)
                .enableExecuteCommand(true)
                .launchType(LaunchType.EC2)
                .build());
    }

    //네트워크 설정
    private static NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .awsvpcConfiguration(AwsVpcConfiguration.builder()
                    .subnets("subnet-0de27c63915bd0893") // 여기에 실제 서브넷 ID 입력
                    .securityGroups("sg-0271e53debeb22431") // 여기에 실제 보안 그룹 ID 입력
                    .assignPublicIp(AssignPublicIp.DISABLED) // 필요에 따라 변경
                    .build())
            .build();

    /**
     * 볼륨 생성하는 메서드
     * /home/projects/{memberId}/{projectId}를 기준으로 잡음
     */
    private Volume createVolume(Long memberId, Long projectId) {
        return Volume.builder()
                .name("project-volume")
                .host(HostVolumeProperties.builder().sourcePath("/home/projects/" + memberId + "/" + projectId).build())
                .build();
    }

    private LogConfiguration createLogConfiguration() {
        return LogConfiguration.builder()
                .logDriver(LogDriver.AWSLOGS)
                .options(Map.of(
                        "awslogs-group", "/ecs/", // CloudWatch 로그 그룹 지정
                        "awslogs-region", "ap-northeast-2", // 로그 그룹이 위치한 리전
                        "awslogs-stream-prefix", "ecs" // 로그 스트림 접두사
                ))
                .build();
    }

    /**
     * 컨테이너 정의 생성
     */
    private ContainerDefinition createContainerDefinition(String containerImage, LogConfiguration logConfiguration) {
        return ContainerDefinition.builder()
                .name("project-container")
                .image(containerImage)
                .cpu(256)
                .memory(300)
                .memoryReservation(300)
                .command("/bin/sh", "-c", "while true; do echo hello; sleep infinity; done")
                .essential(true)
                .logConfiguration(logConfiguration)
                .mountPoints(MountPoint.builder().sourceVolume("project-volume").containerPath("/home/projects").readOnly(false).build())
//                .linuxParameters(linux -> linux.initProcessEnabled(true))
                .build();
    }

    /**
     * 태스크 정의 생성하고 등록까지
     */
    private RegisterTaskDefinitionResponse registerTaskDefinition(Volume volume, ContainerDefinition containerDefinition) {
        return ecsClient.registerTaskDefinition(RegisterTaskDefinitionRequest.builder()
                .family(TASK_DEFINITION_FAMILY)
                .volumes(volume)
                .cpu("256")
                .memory("300")
                .networkMode(NetworkMode.AWSVPC)
                .requiresCompatibilities(Compatibility.EC2)
                .executionRoleArn("arn:aws:iam::535862911481:role/ecsTaskExecutionRole")
                .taskRoleArn("arn:aws:iam::535862911481:role/taskRole")
                .containerDefinitions(containerDefinition)
                .build());
    }



    private static String selectContainerImageByLanguage(ProjectLanguage language) {
        switch (language) {
            case JAVA:
                return "openjdk:11"; // Java 이미지
            case PYTHON:
                return "python:3"; // Python 이미지
            case JAVASCRIPT:
                return "node:14"; // Node.js 이미지
            case CPP:
                return "gcc:latest"; // C++ 이미지
            case C:
                return "gcc:latest"; // C 이미지
            case KOTLIN:
                return "openjdk:11"; // JAVA 이미지
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }
}
