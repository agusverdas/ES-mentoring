package com.epam.esm.service.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.repository.Repository;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.dto.GiftCertificateDTO;
import com.epam.esm.service.dto.TagDTO;
import com.epam.esm.service.exception.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.epam.esm.service.CertificateDatabaseSpecification.*;
import static com.epam.esm.service.TagDatabaseSpecifications.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

@Service
public class GiftCertificateServiceDatabase implements GiftCertificateService {
    private static Logger logger = LogManager.getLogger();
    private Repository<GiftCertificate> certificateRepository;
    private Repository<Tag> tagRepository;
    private final ModelMapper modelMapper;

    public GiftCertificateServiceDatabase(Repository<GiftCertificate> certificateRepository,
                                          Repository<Tag> tagRepository, ModelMapper modelMapper) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public GiftCertificateDTO create(GiftCertificateDTO certificateDTO) {
        logger.debug("CERTIFICATE SERVICE: create");
        GiftCertificate certificate = modelMapper.map(certificateDTO, GiftCertificate.class);
        Set<Tag> tags = certificate.getTags();
        Set<Tag> notPresentTags = tags.stream()
                .filter(tag -> tagRepository.queryFromDatabase(
                        findTagByName(tag.getName())).isEmpty())
                .collect(toSet());
        tags.removeAll(notPresentTags);
        Set<Tag> certificateTags = notPresentTags.stream()
                .map(tagRepository::create)
                .collect(toSet());
        tags.stream()
                .map(tag -> tagRepository.queryFromDatabase(
                        findTagByName(tag.getName())).get(0))
                .forEach(certificateTags::add);
        certificate.setTags(certificateTags);
        certificate.setCreationDate(LocalDate.now());
        GiftCertificate created = certificateRepository.create(certificate);
        List<Tag> createdCertificateTags = tagRepository.queryFromDatabase(findTagsByCertificate(created));
        created.setTags(new HashSet<>(createdCertificateTags));
        return modelMapper.map(created, GiftCertificateDTO.class);
    }

    @Transactional
    @Override
    public Integer delete(Long id) {
        logger.debug("CERTIFICATE SERVICE: delete");
        return certificateRepository.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GiftCertificateDTO> findAll() {
        logger.debug("CERTIFICATE SERVICE: findAll");
        List<GiftCertificate> certificates = certificateRepository.findAll();
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Transactional
    @Override
    public Integer update(GiftCertificateDTO certificateDTO) {
        logger.debug("CERTIFICATE SERVICE: update");
        GiftCertificate certificate = modelMapper.map(certificateDTO, GiftCertificate.class);
        certificate.setModificationDate(LocalDate.now());
        Set<Tag> tags = certificateDTO.getTags().stream()
                .map(tag -> tagRepository.queryFromDatabase(findTagByName(tag.getName())).get(0))
                .collect(toSet());
        certificate.setTags(tags);
        return certificateRepository.update(certificate);
    }

    @Transactional(readOnly = true)
    @Override
    public GiftCertificateDTO findById(Long id) throws EntityNotFoundException {
        logger.debug("CERTIFICATE SERVICE: findById");
        List<GiftCertificate> selected = certificateRepository.findById(id);
        if (selected.isEmpty()){
            throw new EntityNotFoundException("No tag with such id.");
        }
        GiftCertificate certificate = selected.get(0);
        eager(certificate);
        return modelMapper.map(certificate, GiftCertificateDTO.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GiftCertificateDTO> findByTag(String name) {
        logger.debug("CERTIFICATE SERVICE: findByTagSortedByName");
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByTag(name));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GiftCertificateDTO> findSortedByName(boolean asc) {
        logger.debug("CERTIFICATE SERVICE: findSortedByName");
        List<GiftCertificate> certificates = certificateRepository.queryFromDatabase(certificatesSortedByName(asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GiftCertificateDTO> findSortedByDate(boolean asc) {
        logger.debug("CERTIFICATE SERVICE: findSortedByDate");
        List<GiftCertificate> certificates = certificateRepository.queryFromDatabase(certificatesSortedByDate(asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GiftCertificateDTO> findByTagSortedByName(String name, boolean asc) {
        logger.debug("CERTIFICATE SERVICE: findByTagSortedByName");
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByTagSortedByName(name, asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GiftCertificateDTO> findByTagSortedByDate(String name, boolean asc) {
        logger.debug("CERTIFICATE SERVICE: findByTagSortedByDate");
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByTagSortedByDate(name, asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Override
    public List<GiftCertificateDTO> findByNamePart(String part) {
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByNamePart("%"+part+"%"));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Override
    public List<GiftCertificateDTO> findByDescriptionPart(String part) {
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByDescriptionPart("%"+part+"%"));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Override
    public List<GiftCertificateDTO> findByNamePartSortedByName(String part, boolean asc) {
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByNamePartSortedByName("%"+part+"%", asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Override
    public List<GiftCertificateDTO> findByNamePartSortedByDate(String part, boolean asc) {
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByNamePartSortedByDate("%"+part+"%", asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Override
    public List<GiftCertificateDTO> findByDescriptionPartSortedByDate(String part, boolean asc) {
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByDescriptionPartSortedByDate("%"+part+"%", asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Override
    public List<GiftCertificateDTO> findByDescriptionPartSortedByName(String part, boolean asc) {
        List<GiftCertificate> certificates =
                certificateRepository.queryFromDatabase(certificatesByDescriptionPartSortedByName("%"+part+"%", asc));
        certificates.forEach(this::eager);
        return certificates.stream()
                .map(certificate -> modelMapper.map(certificate, GiftCertificateDTO.class))
                .collect(toList());
    }

    @Override
    public List<GiftCertificateDTO> findByTagByNamePartSortedByName(String tag, String part, boolean asc) {
        return null;
    }

    @Override
    public List<GiftCertificateDTO> findByTagByNamePartSortedByDate(String tag, String part, boolean asc) {
        return null;
    }

    @Override
    public List<GiftCertificateDTO> findByTagByDescriptionPartSortedByName(String tag, String part, boolean asc) {
        return null;
    }

    @Override
    public List<GiftCertificateDTO> findByTagByDescriptionPartSortedByDate(String tag, String part, boolean asc) {
        return null;
    }

    private void eager(GiftCertificate certificate) {
        Set<Tag> tags = new HashSet<>(tagRepository.queryFromDatabase(findTagsByCertificate(certificate)));
        certificate.setTags(tags);
    }
}
