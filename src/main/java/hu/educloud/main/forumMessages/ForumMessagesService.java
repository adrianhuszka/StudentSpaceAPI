package hu.educloud.main.forumMessages;

import hu.educloud.main.common.IServiceSimple;
import hu.educloud.main.errors.NotFoundException;
import hu.educloud.main.forum.ForumRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForumMessagesService {
    private final ForumMessagesRepository forumMessagesRepository;
    private final ForumRepository forumRepository;

    public List<ForumMessages> getAll() {
        return forumMessagesRepository.findAll();
    }

    public ForumMessages findById(@NonNull UUID id) {
        return forumMessagesRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    public String save(@NonNull ForumMessagesRequest forumMessagesRequest) {
        var forum = forumRepository.findById(UUID.fromString(forumMessagesRequest.forumId()))
                .orElseThrow(() -> new NotFoundException(forumMessagesRequest.forumId()));

        var forumMessages = ForumMessages.builder()
                .message(forumMessagesRequest.message())
                .forum(forum)
                .build();

        return forumMessagesRepository.save(forumMessages).getId().toString();
    }

    public String update(@NonNull ForumMessagesRequest forumMessagesRequest) {
        var existing = forumMessagesRepository.findById(UUID.fromString(forumMessagesRequest.id()))
                .orElseThrow(() -> new NotFoundException(forumMessagesRequest.id()));

        existing.setMessage(forumMessagesRequest.message());

        return forumMessagesRepository.save(existing).getId().toString();
    }

    public void delete(@NonNull UUID id) {
        forumMessagesRepository.deleteById(id);
    }
}

