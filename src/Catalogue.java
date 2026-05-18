import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Catalogue implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Publication> publications;

    public Catalogue() {
        this.publications = new ArrayList<>();
    }

    public void addPublication(Publication p) {
        if (p == null || p.getTitle() == null || p.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Publication title cannot be empty.");
        }
        if (findPublicationByTitle(p.getTitle()) != null) {
            throw new IllegalArgumentException("Publication with title '" + p.getTitle() + "' already exists.");
        }
        publications.add(p);
    }

    public void updatePublicationByTitle(String title, Publication updatedPublication) throws BookNotFoundException {
        if (updatedPublication == null || updatedPublication.getTitle() == null
            || updatedPublication.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Updated publication title cannot be empty.");
        }

        int index = -1;
        for (int i = 0; i < publications.size(); i++) {
            Publication current = publications.get(i);
            if (current.getTitle() != null && current.getTitle().equalsIgnoreCase(title)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new BookNotFoundException("Publication with title '" + title + "' was not found.");
        }

        for (int i = 0; i < publications.size(); i++) {
            if (i == index) {
                continue;
            }
            Publication current = publications.get(i);
            if (current.getTitle() != null
                && current.getTitle().equalsIgnoreCase(updatedPublication.getTitle())) {
                throw new IllegalArgumentException(
                    "Publication with title '" + updatedPublication.getTitle() + "' already exists.");
            }
        }

        publications.set(index, updatedPublication);
    }

    public void removePublicationByTitle(String title) throws BookNotFoundException {
        Publication publication = findPublicationByTitle(title);
        if (publication == null) {
            throw new BookNotFoundException("Publication with title '" + title + "' was not found.");
        }
        publications.remove(publication);
    }

    public Publication findPublicationByTitle(String title) {
        for (Publication publication : publications) {
            if (publication.getTitle() != null && publication.getTitle().equalsIgnoreCase(title)) {
                return publication;
            }
        }
        return null;
    }

    public List<Publication> getAllPublications() {
        return new ArrayList<>(publications);
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(this);
        }
    }

    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = inputStream.readObject();
            if (obj instanceof Catalogue) {
                Catalogue loadedCatalogue = (Catalogue) obj;
                this.publications = new ArrayList<>(loadedCatalogue.publications);
            } else {
                throw new IOException("Invalid file content: expected Catalogue.");
            }
        }
    }
}
