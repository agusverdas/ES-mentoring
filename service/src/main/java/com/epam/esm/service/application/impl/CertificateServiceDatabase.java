package com.epam.esm.service.application.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.Repository;
import com.epam.esm.repository.specification.Specification;
import com.epam.esm.repository.specification.SpecificationBuilder;
import com.epam.esm.service.application.CertificateService;
import com.epam.esm.service.application.DatabaseSpecificationCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CertificateServiceDatabase implements CertificateService {
    private static Logger logger = LogManager.getLogger();

    private final DatabaseSpecificationCreator specificationCreator;

    private final Repository<GiftCertificate> certificateRepository;
    private final Repository<Tag> tagRepository;

    @Autowired
    public CertificateServiceDatabase(Repository<GiftCertificate> certificateRepository,
                                      Repository<Tag> tagRepository,
                                      DatabaseSpecificationCreator specificationCreator) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
        this.specificationCreator = specificationCreator;
    }

    @Override
    public List<GiftCertificate> findByTag(Tag tag) {
        return null;
    }

    @Override
    public List<GiftCertificate> findByTags(Collection<Tag> tags) {
        return null;
    }

    @Override
    public List<GiftCertificate> findByNamePart(String part, boolean eager) {
        List<GiftCertificate> certificates = certificateRepository
                .queryFromDatabase(specificationCreator.certificatesByNamePart(part));
        if(eager) {
            certificates.forEach(this::eagerFind);
        }
        return certificates;
    }

    @Override
    public List<GiftCertificate> findByDescriptionPart(String part, boolean eager) {
        List<GiftCertificate> certificates = certificateRepository
                .queryFromDatabase(specificationCreator.certificatesByDescriptionPart(part));
        if(eager) {
            certificates.forEach(this::eagerFind);
        }
        return certificates;
    }

    @Override
    public List<GiftCertificate> findSortedByDate(boolean eager) {
        List<GiftCertificate> certificates = certificateRepository
                .queryFromDatabase(specificationCreator.certificatesSortedByDate());
        if(eager) {
            certificates.forEach(this::eagerFind);
        }
        return certificates;
    }

    @Override
    public List<GiftCertificate> findSortedByName(boolean eager) {
        List<GiftCertificate> certificates = certificateRepository
                .queryFromDatabase(specificationCreator.certificatesSortedByName());
        if(eager){
            certificates.forEach(this::eagerFind);
        }
        return certificates;
    }

    @Override
    public Long create(GiftCertificate giftCertificate) {
        logger.debug("Creating new certificate...");
        Set<Tag> tags = giftCertificate.getTagSet();
        Set<Tag> notPresentTags = tags.stream().filter(tag ->
            tagRepository.queryFromDatabase(specificationCreator.tagByName(tag.getName())).isEmpty()
        ).collect(Collectors.toSet());
        tags.removeAll(notPresentTags);
        Set<Tag> certificateTags = new HashSet<>();
        notPresentTags.stream().map(tagRepository::create).forEach(certificateTags::add);
        tags.stream().map(tag ->
            tagRepository.queryFromDatabase(specificationCreator.tagByName(tag.getName())).get(0)
        ).forEach(certificateTags::add);
        giftCertificate.setTagSet(certificateTags);
        return certificateRepository.create(giftCertificate).getId();
    }

    @Override
    public Integer remove(GiftCertificate giftCertificate) {
        return certificateRepository.remove(giftCertificate);
    }

    @Override
    public Integer update(GiftCertificate giftCertificate) {
        return certificateRepository.update(giftCertificate);
    }

    @Override
    public List<GiftCertificate> findAll(boolean eager) {
        logger.debug("Finding all the gift certificates...");
        List<GiftCertificate> certificates = certificateRepository
                .queryFromDatabase(specificationCreator.findAllGifts());
        if (eager){
            certificates.forEach(this::eagerFind);
        }
        return certificates;
    }

    @Override
    public GiftCertificate findById(Long id, boolean eager) {
        logger.debug("Find certificate by id...");
        List<GiftCertificate> certificates = certificateRepository
                .queryFromDatabase(specificationCreator.findById(id, GiftCertificate.class));
        GiftCertificate certificate = certificates.isEmpty() ? null : certificates.get(0);
        if(certificate != null && !eager){
            eagerFind(certificate);
        }
        return certificate;
    }

    @Override
    public List<GiftCertificate> findByNamePart(String part) {
        return findByNamePart(part, false);
    }

    @Override
    public List<GiftCertificate> findByDescriptionPart(String part) {
        return findByDescriptionPart(part, false);
    }

    @Override
    public List<GiftCertificate> findSortedByDate() {
        return findSortedByDate(false);
    }

    @Override
    public List<GiftCertificate> findSortedByName() {
        return findSortedByName(false);
    }

    @Override
    public List<GiftCertificate> findAll() {
        return findAll(false);
    }

    @Override
    public GiftCertificate findById(Long id) { return findById(id, false); }

    private void eagerFind(GiftCertificate certificate){
        logger.debug("Eager find...");
        List<Tag> tags = tagRepository
                .queryFromDatabase(specificationCreator.tagsByCertificate(certificate));
        certificate.setTagSet(new HashSet<>(tags));
    }
}
