package com.junt.checkdomain.api;

import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.Status;
import com.junt.checkdomain.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StatusRestController {
    private final StatusService statusService;

    @PostMapping("/status/save")
    public ResponseEntity<?> saveStatus(@RequestParam("status")int status,@RequestParam("message")String message) {
        Status newStatus = new Status();
        newStatus.setStatus(status);
        newStatus.setMessage(message);
        System.out.println(newStatus);
        Status st = statusService.save(newStatus);
        return ResponseEntity.ok(st);
    }

    @DeleteMapping("/status/delete")
    public ResponseEntity<?> deleteStatus(@RequestParam("status") Integer status) {
        Status st = statusService.getStatusById(status);
        Domain dm = statusService.getDomainByStatus(st);
        if (dm != null) {
            return ResponseEntity.notFound().header("status","constrain").build();
        }
        statusService.deleteStatusById(status);
        return ResponseEntity.ok().build();
    }
}
