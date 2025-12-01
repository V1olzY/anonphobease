package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.Ban;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class BanRepositoryTest {

    @Autowired
    private BanRepository banRepository;

    @Test
    void saveAndFindById_worksCorrectly() {
        Ban ban = new Ban();
        ban.setUserId(new ObjectId());
        ban.setChatId(new ObjectId());
        ban.setBanReason("Test reason");
        ban.setBannedAt(new Date());

        Ban saved = banRepository.save(ban);

        assertNotNull(saved.getId());

        Optional<Ban> foundOpt = banRepository.findById(saved.getId());
        assertTrue(foundOpt.isPresent());

        Ban found = foundOpt.get();
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test reason", found.getBanReason());
        assertEquals(saved.getUserId(), found.getUserId());
        assertEquals(saved.getChatId(), found.getChatId());
        assertNotNull(found.getBannedAt());
    }

    @Test
    void findAll_returnsAllSavedBans() {
        banRepository.deleteAll();

        Ban b1 = new Ban();
        b1.setUserId(new ObjectId());
        b1.setChatId(new ObjectId());
        b1.setBanReason("Reason 1");
        b1.setBannedAt(new Date());

        Ban b2 = new Ban();
        b2.setUserId(new ObjectId());
        b2.setChatId(new ObjectId());
        b2.setBanReason("Reason 2");
        b2.setBannedAt(new Date());

        banRepository.save(b1);
        banRepository.save(b2);

        List<Ban> all = banRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void deleteById_removesBan() {
        Ban ban = new Ban();
        ban.setUserId(new ObjectId());
        ban.setChatId(new ObjectId());
        ban.setBanReason("To delete");
        ban.setBannedAt(new Date());

        Ban saved = banRepository.save(ban);
        ObjectId id = saved.getId();

        banRepository.deleteById(id);

        Optional<Ban> found = banRepository.findById(id);
        assertTrue(found.isEmpty());
    }
}
