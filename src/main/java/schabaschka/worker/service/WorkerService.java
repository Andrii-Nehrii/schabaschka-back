package schabaschka.worker.service;

import org.springframework.data.domain.Page; //changed
import schabaschka.worker.dto.WorkerDto;

import java.util.List;
import java.util.Optional;


public interface WorkerService {


    List<WorkerDto> find(String city, String name, int page, int size); //changed


    Page<WorkerDto> findPage(String city, String name, int page, int size); //changed

    Optional<WorkerDto> findById(long id);
}
