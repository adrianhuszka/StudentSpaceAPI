package hu.educloud.main.forumMessages;

import hu.educloud.main.common.IServiceSimple;
import hu.educloud.main.errors.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForumMessagesService implements IServiceSimple<ForumMessages> {
    private final ForumMessagesRepository forumMessagesRepository;

    @Override
    public List<ForumMessages> getAll() {
        return forumMessagesRepository.findAll();
    }

    @Override
    public ForumMessages findById(@NonNull UUID id) {
        return forumMessagesRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    @Override
    public String save(@NonNull ForumMessages forumMessages) {
        return forumMessagesRepository.save(forumMessages).getId().toString();
    }

    @Override
    public String update(@NonNull ForumMessages forumMessages) {
        var existing = forumMessagesRepository.findById(forumMessages.getId())
                .orElseThrow(() -> new NotFoundException(forumMessages.getId().toString()));

        existing.setMessage(forumMessages.getMessage());
        existing.setAuthor(forumMessages.getAuthor());
        existing.setForum(forumMessages.getForum());

        return forumMessagesRepository.save(existing).getId().toString();
    }

    @Override
    public void delete(@NonNull UUID id) {
        forumMessagesRepository.deleteById(id);
    }
}

