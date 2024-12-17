package bbangbbangz.baby_monitoring_system.service;

import bbangbbangz.baby_monitoring_system.domain.Baby;
import bbangbbangz.baby_monitoring_system.domain.ParentContact;
import bbangbbangz.baby_monitoring_system.domain.User;
import bbangbbangz.baby_monitoring_system.dto.BabyRequest;
import bbangbbangz.baby_monitoring_system.dto.MypageRequest;
import bbangbbangz.baby_monitoring_system.dto.ParentContactRequest;
import bbangbbangz.baby_monitoring_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MypageService {

    private final UserRepository userRepository;

    public MypageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUserDetails(Long userId, MypageRequest mypageRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Baby 정보 등록
        if (mypageRequest.getBaby() != null) {
            Baby baby = new Baby();
            baby.setBabyName(mypageRequest.getBaby().getBabyName());
            baby.setBirthDate(mypageRequest.getBaby().getBirthDate());
            baby.setGender(Baby.Gender.valueOf(mypageRequest.getBaby().getGender().toUpperCase()));
            baby.setUser(user);
            user.setBaby(baby);
        }

        // ParentContact 정보 등록
        if (mypageRequest.getParentContacts() != null) {
            for (var contactRequest : mypageRequest.getParentContacts()) {
                ParentContact contact = new ParentContact();
                contact.setMomPhoneNumber(contactRequest.getMomPhoneNumber());
                contact.setDadPhoneNumber(contactRequest.getDadPhoneNumber());
                contact.setUser(user);
                user.getParentContacts().add(contact);
            }
        }

        userRepository.save(user);
    }

    @Transactional
    public void updateUserDetails(Long userId, MypageRequest updatedMypageRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Baby 정보 수정
        if (updatedMypageRequest.getBaby() != null) {
            Baby baby = user.getBaby();
            if (baby == null) {
                baby = new Baby();
                baby.setUser(user);
                user.setBaby(baby);
            }
            baby.setBabyName(updatedMypageRequest.getBaby().getBabyName());
            baby.setBirthDate(updatedMypageRequest.getBaby().getBirthDate());
            baby.setGender(Baby.Gender.valueOf(updatedMypageRequest.getBaby().getGender().toUpperCase()));
        }

        // ParentContact 정보 수정
        if (updatedMypageRequest.getParentContacts() != null) {
            user.getParentContacts().clear();
            for (var contactRequest : updatedMypageRequest.getParentContacts()) {
                ParentContact contact = new ParentContact();
                contact.setMomPhoneNumber(contactRequest.getMomPhoneNumber());
                contact.setDadPhoneNumber(contactRequest.getDadPhoneNumber());
                contact.setUser(user);
                user.getParentContacts().add(contact);
            }
        }

        userRepository.save(user);
    }

    public MypageRequest getMypage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        MypageRequest mypageRequest = new MypageRequest();
        if (user.getBaby() != null) {
            Baby baby = user.getBaby();
            BabyRequest babyRequest = new BabyRequest();
            babyRequest.setBabyName(baby.getBabyName());
            babyRequest.setBirthDate(baby.getBirthDate());
            babyRequest.setGender(baby.getGender().toString());
            mypageRequest.setBaby(babyRequest);
        }

        if (user.getParentContacts() != null && !user.getParentContacts().isEmpty()) {
            List<ParentContactRequest> parentContactRequests = user.getParentContacts().stream().map(contact -> {
                ParentContactRequest contactRequest = new ParentContactRequest();
                contactRequest.setMomPhoneNumber(contact.getMomPhoneNumber());
                contactRequest.setDadPhoneNumber(contact.getDadPhoneNumber());
                return contactRequest;
            }).toList();
            mypageRequest.setParentContacts(parentContactRequests);
        }

        return mypageRequest;
    }
}
