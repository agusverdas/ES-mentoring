package com.epam.esm.service;

import com.epam.esm.service.dto.GiftCertificateDTO;
import com.epam.esm.service.exception.EntityNotFoundException;

import java.util.List;

public interface GiftCertificateService {
    GiftCertificateDTO create(GiftCertificateDTO t);
    Integer delete(Long id);
    List<GiftCertificateDTO> findAll();
    Integer update(GiftCertificateDTO t);
    GiftCertificateDTO findById(Long id) throws EntityNotFoundException;
}
