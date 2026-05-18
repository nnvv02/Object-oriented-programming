import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.Year;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class BookGUI extends JFrame implements ActionListener {
    private final Catalogue catalogue;
    private final DefaultListModel<String> listModel;
    private final JList<String> bookList;

    private final JTextField titleField;
    private final JTextField authorField;
    private final JTextField publisherField;
    private final JTextField genreField;
    private final JTextField yearField;
    private final JTextField searchField;

    public BookGUI() {
        super("Каталог книг");
        this.catalogue = new Catalogue();
        this.listModel = new DefaultListModel<>();
        this.bookList = new JList<>(listModel);

        this.titleField = new JTextField();
        this.authorField = new JTextField();
        this.publisherField = new JTextField();
        this.genreField = new JTextField();
        this.yearField = new JTextField();
        this.searchField = new JTextField();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(920, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildInputPanel(), BorderLayout.NORTH);
        add(new JScrollPane(bookList), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshList(catalogue.getAllPublications());
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 8, 8));

        panel.add(new JLabel("Назва:"));
        panel.add(titleField);
        panel.add(new JLabel("Автор:"));
        panel.add(authorField);

        panel.add(new JLabel("Видавництво:"));
        panel.add(publisherField);
        panel.add(new JLabel("Жанр:"));
        panel.add(genreField);

        panel.add(new JLabel("Рік:"));
        panel.add(yearField);
        panel.add(new JLabel("Пошук за назвою:"));
        panel.add(searchField);

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));

        JButton addButton = createButton("Додати книгу");
        JButton removeButton = createButton("Видалити книгу");
        JButton updateButton = createButton("Оновити книгу");
        JButton saveButton = createButton("Зберегти у файл");
        JButton loadButton = createButton("Завантажити з файлу");
        JButton searchButton = createButton("Пошук");
        JButton resetSearchButton = createButton("Скинути параметри пошуку");

        panel.add(addButton);
        panel.add(removeButton);
        panel.add(updateButton);
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(searchButton);
        panel.add(resetSearchButton);

        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if ("Додати книгу".equals(command)) {
            addBook();
        } else if ("Видалити книгу".equals(command)) {
            removeBook();
        } else if ("Оновити книгу".equals(command)) {
            updateBook();
        } else if ("Зберегти у файл".equals(command)) {
            saveToFile();
        } else if ("Завантажити з файлу".equals(command)) {
            loadFromFile();
        } else if ("Пошук".equals(command)) {
            searchBooks();
        } else if ("Скинути параметри пошуку".equals(command)) {
            resetSearch();
        }
    }

    private void addBook() {
        try {
            Book book = buildBookFromForm();
            catalogue.addPublication(book);
            refreshList(catalogue.getAllPublications());
            clearForm();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void removeBook() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Введіть назву книги для видалення.");
            return;
        }

        try {
            catalogue.removePublicationByTitle(title);
            refreshList(catalogue.getAllPublications());
        } catch (BookNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateBook() {
        try {
            Book updatedBook = buildBookFromForm();
            catalogue.updatePublicationByTitle(updatedBook.getTitle(), updatedBook);
            refreshList(catalogue.getAllPublications());
        } catch (IllegalArgumentException | BookNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            catalogue.saveToFile(chooser.getSelectedFile().getAbsolutePath());
            showInfo("Каталог успішно збережено.");
        } catch (IOException ex) {
            showError("Не вдалося зберегти каталог: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            catalogue.loadFromFile(chooser.getSelectedFile().getAbsolutePath());
            refreshList(catalogue.getAllPublications());
            showInfo("Каталог успішно завантажено.");
        } catch (IOException | ClassNotFoundException ex) {
            showError("Не вдалося завантажити каталог: " + ex.getMessage());
        }
    }

    private void searchBooks() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshList(catalogue.getAllPublications());
            return;
        }

        List<Publication> all = catalogue.getAllPublications();
        DefaultListModel<String> filtered = new DefaultListModel<>();
        for (Publication publication : all) {
            String title = publication.getTitle();
            if (title != null && title.toLowerCase().contains(query)) {
                filtered.addElement(publication.toString());
            }
        }

        listModel.clear();
        for (int i = 0; i < filtered.size(); i++) {
            listModel.addElement(filtered.get(i));
        }
    }

    private void resetSearch() {
        searchField.setText("");
        refreshList(catalogue.getAllPublications());
    }

    private Book buildBookFromForm() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();
        String genre = genreField.getText().trim();
        String yearText = yearField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || genre.isEmpty() || yearText.isEmpty()) {
            throw new IllegalArgumentException(
                "Усі поля (назва, автор, видавництво, жанр, рік) обов'язкові.");
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Рік має бути коректним числом.");
        }

        int currentYear = Year.now().getValue();
        if (year < 0 || year > currentYear) {
            throw new IllegalArgumentException("Рік має бути в діапазоні від 0 до " + currentYear + ".");
        }

        return new Book(title, year, author, publisher, genre);
    }

    private void refreshList(List<Publication> publications) {
        listModel.clear();
        for (Publication publication : publications) {
            listModel.addElement(publication.toString());
        }
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        genreField.setText("");
        yearField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Інформація", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookGUI gui = new BookGUI();
            gui.setVisible(true);
        });
    }
}
