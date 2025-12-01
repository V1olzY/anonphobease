package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.config.DataSeeder;
import com.vizako.anonphobease.config.MongoConfig;
import com.vizako.anonphobease.model.Report;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(MongoConfig.class)
@ActiveProfiles("test")
class ReportRepositoryTest {

    @MockitoBean
    private DataSeeder dataSeeder;

    @Autowired
    private ReportRepository reportRepository;

    @BeforeEach
    void setup() {
        reportRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        ObjectId chatId = new ObjectId();
        ObjectId reportedUserId = new ObjectId();
        ObjectId reporterUserId = new ObjectId();
        ObjectId messageId = new ObjectId();

        Report report = new Report();
        report.setChatId(chatId);
        report.setReportedUserId(reportedUserId);
        report.setReporterUserId(reporterUserId);
        report.setMessageId(messageId);
        report.setReason("Offensive message");

        Report saved = reportRepository.save(report);

        Optional<Report> found = reportRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(chatId, found.get().getChatId());
        assertEquals(reportedUserId, found.get().getReportedUserId());
        assertEquals(reporterUserId, found.get().getReporterUserId());
        assertEquals(messageId, found.get().getMessageId());
        assertEquals("Offensive message", found.get().getReason());
        assertFalse(found.get().getIsResolved());
    }

    @Test
    void updateReport_markAsResolved() {
        ObjectId chatId = new ObjectId();
        ObjectId reportedUserId = new ObjectId();
        ObjectId reporterUserId = new ObjectId();
        ObjectId messageId = new ObjectId();
        ObjectId moderatorId = new ObjectId();

        Report report = new Report();
        report.setChatId(chatId);
        report.setReportedUserId(reportedUserId);
        report.setReporterUserId(reporterUserId);
        report.setMessageId(messageId);
        report.setReason("Spam");

        Report saved = reportRepository.save(report);

        saved.setIsResolved(true);
        saved.setResolvedAt(new Date());
        saved.setModeratorId(moderatorId);
        saved.setActionReason("User warned");

        Report updated = reportRepository.save(saved);

        Optional<Report> found = reportRepository.findById(updated.getId());

        assertTrue(found.isPresent());
        assertTrue(found.get().getIsResolved());
        assertNotNull(found.get().getResolvedAt());
        assertEquals(moderatorId, found.get().getModeratorId());
        assertEquals("User warned", found.get().getActionReason());
    }

    @Test
    void deleteReport() {
        Report report = new Report();
        report.setChatId(new ObjectId());
        report.setReportedUserId(new ObjectId());
        report.setReporterUserId(new ObjectId());
        report.setMessageId(new ObjectId());
        report.setReason("To delete");

        Report saved = reportRepository.save(report);

        reportRepository.deleteById(saved.getId());

        Optional<Report> found = reportRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void findByChatId_returnsOnlyReportsFromThatChat() {
        ObjectId chat1 = new ObjectId();
        ObjectId chat2 = new ObjectId();

        Report r1 = new Report();
        r1.setChatId(chat1);
        r1.setReportedUserId(new ObjectId());
        r1.setReporterUserId(new ObjectId());
        r1.setMessageId(new ObjectId());
        r1.setReason("Reason 1");

        Report r2 = new Report();
        r2.setChatId(chat1);
        r2.setReportedUserId(new ObjectId());
        r2.setReporterUserId(new ObjectId());
        r2.setMessageId(new ObjectId());
        r2.setReason("Reason 2");

        Report r3 = new Report();
        r3.setChatId(chat2);
        r3.setReportedUserId(new ObjectId());
        r3.setReporterUserId(new ObjectId());
        r3.setMessageId(new ObjectId());
        r3.setReason("Reason 3");

        reportRepository.save(r1);
        reportRepository.save(r2);
        reportRepository.save(r3);

        List<Report> result = reportRepository.findByChatId(chat1);

        assertEquals(2, result.size());
        List<String> reasons = result.stream().map(Report::getReason).toList();
        assertTrue(reasons.contains("Reason 1"));
        assertTrue(reasons.contains("Reason 2"));
    }

    @Test
    void findByReportedUserId_returnsOnlyReportsForThatUser() {
        ObjectId user1 = new ObjectId();
        ObjectId user2 = new ObjectId();

        Report r1 = new Report();
        r1.setChatId(new ObjectId());
        r1.setReportedUserId(user1);
        r1.setReporterUserId(new ObjectId());
        r1.setMessageId(new ObjectId());
        r1.setReason("User1 report 1");

        Report r2 = new Report();
        r2.setChatId(new ObjectId());
        r2.setReportedUserId(user1);
        r2.setReporterUserId(new ObjectId());
        r2.setMessageId(new ObjectId());
        r2.setReason("User1 report 2");

        Report r3 = new Report();
        r3.setChatId(new ObjectId());
        r3.setReportedUserId(user2);
        r3.setReporterUserId(new ObjectId());
        r3.setMessageId(new ObjectId());
        r3.setReason("User2 report");

        reportRepository.save(r1);
        reportRepository.save(r2);
        reportRepository.save(r3);

        List<Report> result = reportRepository.findByReportedUserId(user1);

        assertEquals(2, result.size());
        List<String> reasons = result.stream().map(Report::getReason).toList();
        assertTrue(reasons.contains("User1 report 1"));
        assertTrue(reasons.contains("User1 report 2"));
    }

    @Test
    void findByIsResolved_false_returnsOnlyUnresolvedReports() {
        Report unresolved1 = new Report();
        unresolved1.setChatId(new ObjectId());
        unresolved1.setReportedUserId(new ObjectId());
        unresolved1.setReporterUserId(new ObjectId());
        unresolved1.setMessageId(new ObjectId());
        unresolved1.setReason("Unresolved 1");
        unresolved1.setIsResolved(false);

        Report unresolved2 = new Report();
        unresolved2.setChatId(new ObjectId());
        unresolved2.setReportedUserId(new ObjectId());
        unresolved2.setReporterUserId(new ObjectId());
        unresolved2.setMessageId(new ObjectId());
        unresolved2.setReason("Unresolved 2");
        unresolved2.setIsResolved(false);

        Report resolved = new Report();
        resolved.setChatId(new ObjectId());
        resolved.setReportedUserId(new ObjectId());
        resolved.setReporterUserId(new ObjectId());
        resolved.setMessageId(new ObjectId());
        resolved.setReason("Resolved");
        resolved.setIsResolved(true);

        reportRepository.save(unresolved1);
        reportRepository.save(unresolved2);
        reportRepository.save(resolved);

        List<Report> result = reportRepository.findByIsResolved(false);

        assertEquals(2, result.size());
        List<String> reasons = result.stream().map(Report::getReason).toList();
        assertTrue(reasons.contains("Unresolved 1"));
        assertTrue(reasons.contains("Unresolved 2"));
    }

    @Test
    void findByIsResolved_true_returnsOnlyResolvedReports() {
        Report unresolved = new Report();
        unresolved.setChatId(new ObjectId());
        unresolved.setReportedUserId(new ObjectId());
        unresolved.setReporterUserId(new ObjectId());
        unresolved.setMessageId(new ObjectId());
        unresolved.setReason("Unresolved");
        unresolved.setIsResolved(false);

        Report resolved1 = new Report();
        resolved1.setChatId(new ObjectId());
        resolved1.setReportedUserId(new ObjectId());
        resolved1.setReporterUserId(new ObjectId());
        resolved1.setMessageId(new ObjectId());
        resolved1.setReason("Resolved 1");
        resolved1.setIsResolved(true);

        Report resolved2 = new Report();
        resolved2.setChatId(new ObjectId());
        resolved2.setReportedUserId(new ObjectId());
        resolved2.setReporterUserId(new ObjectId());
        resolved2.setMessageId(new ObjectId());
        resolved2.setReason("Resolved 2");
        resolved2.setIsResolved(true);

        reportRepository.save(unresolved);
        reportRepository.save(resolved1);
        reportRepository.save(resolved2);

        List<Report> result = reportRepository.findByIsResolved(true);

        assertEquals(2, result.size());
        List<String> reasons = result.stream().map(Report::getReason).toList();
        assertTrue(reasons.contains("Resolved 1"));
        assertTrue(reasons.contains("Resolved 2"));
    }

    @Test
    void createdAt_isSetOnSave() {
        Report report = new Report();
        report.setChatId(new ObjectId());
        report.setReportedUserId(new ObjectId());
        report.setReporterUserId(new ObjectId());
        report.setMessageId(new ObjectId());
        report.setReason("Check createdAt");

        Report saved = reportRepository.save(report);

        assertNotNull(saved.getCreatedAt());
    }
}
