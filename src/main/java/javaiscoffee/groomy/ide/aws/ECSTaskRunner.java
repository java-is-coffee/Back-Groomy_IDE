package javaiscoffee.groomy.ide.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.ContainerOverride;
import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.RunTaskRequest;
import com.amazonaws.services.ecs.model.RunTaskResult;
import com.amazonaws.services.ecs.model.TaskOverride;

public class ECSTaskRunner {
    public void ECSCreate(String[] args) {
        // AmazonECS 클라이언트 생성
        AmazonECS ecsClient = AmazonECSClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).build();

        // memberId와 projectId 설정
        String memberId = String.valueOf(1);
        String projectId = String.valueOf(9);

        // RunTaskRequest 객체 생성 및 구성
        RunTaskRequest runTaskRequest = new RunTaskRequest()
                .withCluster("groomy-cluster")
                .withTaskDefinition("groomy-user-container:1")
                .withCount(1)
                .withLaunchType("EC2") // 또는 "FARGATE"
                .withOverrides(new TaskOverride()
                        .withContainerOverrides(new ContainerOverride()
                                .withName(memberId+"/"+projectId)
                                .withEnvironment(new KeyValuePair()
                                        .withName("PROJECT_PATH")
                                        .withValue("/projects/" + memberId + "/" + projectId))));

        // 태스크 실행
        RunTaskResult runTaskResult = ecsClient.runTask(runTaskRequest);

        // 결과 출력
        System.out.println(runTaskResult.toString());
    }
}
