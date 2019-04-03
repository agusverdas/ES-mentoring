package com.epam.esm.service.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.dto.TagMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
public class TagServiceDatabase implements TagService {
    private TagRepository tagRepository;

    public TagServiceDatabase(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    @Override
    public TagDto create(TagDto tagDTO) {
        Tag tag = TagMapper.INSTANCE.tagDtoToTag(tagDTO);
        tagRepository.create(tag);
        return TagMapper.INSTANCE.tagToTagDto(tag);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<TagDto> findAll() {
        return tagRepository.findAll()
                .map(TagMapper.INSTANCE::tagToTagDto)
                .collect(toSet());
    }

    @Transactional(readOnly = true)
    @Override
    public TagDto findById(Long id) {
        Tag tag = tagRepository.findById(id);
        return TagMapper.INSTANCE.tagToTagDto(tag);
    }

    @Transactional(readOnly = true)
    @Override
    public TagDto findByName(String name) {
        Tag tag = tagRepository.findTagByName(name);
        return TagMapper.INSTANCE.tagToTagDto(tag);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }
}
