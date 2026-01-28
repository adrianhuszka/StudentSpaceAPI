package hu.studentspace.main.forum;

import hu.studentspace.main.common.IServiceSimple;
import hu.studentspace.main.errors.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForumService implements IServiceSimple<Forum> {
    private final ForumRepository forumRepository;

    @Override
    public List<Forum> getAll() {
        return forumRepository.findAll();
    }

    @Override
    public Forum findById(@NonNull UUID id) {
        return forumRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    @Override
    public String save(@NonNull Forum forum) {
        return forumRepository.save(forum).getId().toString();
    }

    @Override
    public String update(@NonNull Forum forum) {
        var existing = forumRepository.findById(forum.getId())
                .orElseThrow(() -> new NotFoundException(forum.getId().toString()));

        existing.setForumMessages(forum.getForumMessages());
        existing.setSubject(forum.getSubject());

        return forumRepository.save(existing).getId().toString();
    }

    @Override
    public void delete(@NonNull UUID id) {
        forumRepository.deleteById(id);
    }
}
