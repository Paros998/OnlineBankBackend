package com.OBS.service;

import com.OBS.entity.Announcement;
import com.OBS.repository.AnnouncementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public Announcement getAnnouncement(Long id) {
        return announcementRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Can't find announcement of given id")
        );
    }

    public List<Announcement> getAnnouncements() {
        return announcementRepository.findAll();
    }

    public void addAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    @Transactional
    public void updateAnnouncement(Long id, Announcement announcement) {
        Announcement oldAnnouncement = announcementRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Can't find announcement of given id")
        );
        oldAnnouncement.setAnnouncement(announcement.getAnnouncement());
        announcementRepository.save(oldAnnouncement);
    }

    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new IllegalStateException("Can't find announcement of given id");
        }
        announcementRepository.deleteById(id);
    }

    public void deleteAll(){
        announcementRepository.deleteAll();
    }
}
