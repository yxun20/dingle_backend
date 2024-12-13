package bbangbbangz.baby_monitoring_system.service;

import bbangbbangz.baby_monitoring_system.domain.ParentContact;
import bbangbbangz.baby_monitoring_system.dto.ParentContactDTO;
import bbangbbangz.baby_monitoring_system.repository.ParentContactRepository;
import org.springframework.stereotype.Service;

@Service
public class ParentContactService {

    private final ParentContactRepository parentContactRepository;

    public ParentContactService(ParentContactRepository parentContactRepository) {
        this.parentContactRepository = parentContactRepository;
    }

    public void saveParentContacts(ParentContactDTO parentContactDTO) {
        ParentContact parentContact = new ParentContact();
        parentContact.setMomPhoneNumber(parentContactDTO.getMomPhoneNumber());
        parentContact.setDadPhoneNumber(parentContactDTO.getDadPhoneNumber());
        parentContactRepository.save(parentContact);
    }
}
