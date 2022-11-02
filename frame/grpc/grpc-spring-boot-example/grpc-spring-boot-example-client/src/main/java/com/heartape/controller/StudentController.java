package com.heartape.controller;

import com.heartape.protobuf.StudentProto;
import com.heartape.protobuf.StudentServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class StudentController {

    @GrpcClient("student")
    private StudentServiceGrpc.StudentServiceBlockingStub studentService;

    @GetMapping
    public void list() {
        System.out.println(studentService.one(StudentProto.Request.newBuilder().setId(1).setSex("nan").build()));
    }
}
