package com.heartape.service;

import com.heartape.protobuf.StudentProto;
import com.heartape.protobuf.StudentServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * StudentServiceGrpc.StudentServiceImplBase通过grpc框架依赖student.proto生成
 * <li>
 *     0.在pom文件配置插件protobuf-maven-plugin、os-maven-plugin
 * </li>
 * <li>
 *     1.步骤在main.proto目录下创建student.proto
 * </li>
 * <li>
 *     2.编译StudentProto文件: maven -> 当前maven项目 -> Plugins -> protobuf -> protobuf:compile
 * </li>
 * <li>
 *     3.编译StudentServiceGrpc文件: maven -> 当前maven项目 -> Plugins -> protobuf -> protobuf:compile-custom
 * </li>
 */
@GrpcService
public class StudentService extends StudentServiceGrpc.StudentServiceImplBase {

    @Override
    public void one(StudentProto.Request request, StreamObserver<StudentProto.Response> responseObserver) {
        System.out.println(request);
        StudentProto.Response response = StudentProto.Response
                .newBuilder()
                .setCode(200)
                .setMessage("success")
                .setData(StudentProto.Data
                        .newBuilder()
                        .setName("heartape")
                        .setPhone(8888)
                        .addScore(100)
                        .addScore(100)
                        .addScore(99)
                        .build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
