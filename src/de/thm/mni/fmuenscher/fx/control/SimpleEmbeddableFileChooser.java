package de.thm.mni.fmuenscher.fx.control;

import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Path;

public class SimpleEmbeddableFileChooser extends SplitPane {
    private Path rootDirItem;
    private ItemSelectedListener listener;

    public SimpleEmbeddableFileChooser(ItemSelectedListener listener) {
        this(File.listRoots()[0].toPath(), listener);
    }

    public SimpleEmbeddableFileChooser(Path rootDirItem, ItemSelectedListener listener) {
        this.rootDirItem = rootDirItem;
        this.listener = listener;
        initComponent();
    }

    private void initComponent(){
        ListView<File> rootsView = generateRoots();

        TreeView<File> fileTree = generateFileTree(rootDirItem);

        this.getItems().add(0, rootsView);
        this.getItems().add(1, fileTree);
        this.setDividerPositions(0.2);
    }

    private ListView<File> generateRoots(){
        ListView<File> rootsView = new ListView<>();
        rootsView.getItems().addAll(File.listRoots());
        rootsView.getItems().add(new File(System.getProperty("user.home")));
        rootsView.getSelectionModel().selectedItemProperty().addListener((observableValue, old, newFile) -> {
            rootDirItem = newFile.toPath();
            this.getItems().set(1, generateFileTree(rootDirItem));
        });
        return rootsView;
    }

    private TreeView<File> generateFileTree(Path root){
        TreeView<File> fileTree = new TreeView<File>(
                new SimpleFileTreeItem(root.toFile()));
        fileTree.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            public TreeCell<File> call(TreeView<File> tv) {
                return new TreeCell<File>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null){
                            setText("");
                        } else {
                            if(item.getName().equals("")){
                                setText(File.separator);
                            } else {
                                setText(item.getName());
                            }
                        }
                    }

                };
            }
        });
        fileTree.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, fileTreeItem, t1) -> {
                    if(observableValue.getValue().isLeaf()){
                        listener.onItemSelected(observableValue.getValue().getValue().toPath());
                    }
                });

        return fileTree;
    }

    public interface ItemSelectedListener{
        public void onItemSelected(Path file);
    }
}
