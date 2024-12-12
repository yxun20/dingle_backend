package bbangbbangz.baby_monitoring_system.service;

import bbangbbangz.baby_monitoring_system.dto.BabyStatusDTO;
import org.springframework.stereotype.Service;

@Service
public class BabyService {

    public BabyStatusDTO getBabyStatus() {
        // Replace with actual logic to fetch data from the database
        BabyStatusDTO statusDTO = new BabyStatusDTO();
        statusDTO.setStatus("Baby is sleeping");
        return statusDTO;
    }

    public void saveBabyStatus(BabyStatusDTO statusDTO) {
        // Replace with actual logic to save data to the database
        System.out.println("Saving status: " + statusDTO.getStatus());
    }
}
