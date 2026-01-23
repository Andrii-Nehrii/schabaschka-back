package schabaschka.worker.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schabaschka.worker.dto.WorkerDto;
import schabaschka.worker.service.WorkerService;

import java.util.Optional;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public Page<WorkerDto> list(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return workerService.findPage(city, name, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerDto> getById(@PathVariable("id") long id) {
        Optional<WorkerDto> workerOpt = workerService.findById(id);
        return workerOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
