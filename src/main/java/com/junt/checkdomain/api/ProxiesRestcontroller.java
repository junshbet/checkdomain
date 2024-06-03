package com.junt.checkdomain.api;

import com.junt.checkdomain.model.Check;
import com.junt.checkdomain.model.Proxy;
import com.junt.checkdomain.request.ProxyRequest;
import com.junt.checkdomain.service.CheckService;
import com.junt.checkdomain.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProxiesRestcontroller {
    private final ProxyService proxyService;
    private final CheckService checkService;

    @PostMapping("/proxy/save")
    public ResponseEntity<Proxy> save(@RequestBody ProxyRequest request) {
        System.out.println(request);

        Proxy proxy = proxyService.getProxyByName(request.getName());
        if (request.getId().isBlank() & proxy != null) {
            return ResponseEntity.badRequest().header("status","unique").build();
        }
        Proxy pro = new Proxy();
        if (!request.getId().isBlank()){
            pro.setId(Long.parseLong(request.getId()));
        }
        pro.setName(request.getName());
        pro.setProxy(request.getProxy());
        pro.setPort(request.getPort());
        pro.setUsername(request.getUsername());
        pro.setPassword(request.getPassword());
        return ResponseEntity.ok(proxyService.save(pro));
    }
    @DeleteMapping("/proxy/delete")
    public ResponseEntity<Proxy> delete(@RequestParam("id")long id) {
        Proxy proxy = proxyService.getProxyById(id);
        Check check = checkService.getCheckByProxy(proxy);
        if (check != null) {
            return ResponseEntity.badRequest().header("status","constrain").build();
        }
        proxyService.deleteProxyById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/proxy/getAll")
    public ResponseEntity<List<Proxy>> getAll() {
        return ResponseEntity.ok(proxyService.getAllProxies());
    }
}
