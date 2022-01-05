package gui.sklepview;

import gui.ShopCreatorModel;
import gui.ShopCreatorView.ShopCreatorView;
import kontrolery.ShopCreatorController;
import sklepy.Sklep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import static gui.ImageResizer.getScaledInstance;

// Główny widok, przez który będziemy wywoływać widoki sklepów
public class ApplicationView extends JFrame {

    private final String pathToIcons = "images/shopicons/";

    private JToolBar buttonBar = new JToolBar();
    private Map<AbstractSklepView, String> sklepyINazwyIkon;

    public ApplicationView(Map<AbstractSklepView, String> sklepyINazwyIkon){

        setSize(800, 800);
        setLocationRelativeTo(null);
        this.sklepyINazwyIkon = sklepyINazwyIkon;
        setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Dodaje niewidoczne komponenty, które zajmują przestrzeń: będziemy
        // dodawać obiekty 'JButton' pomiędzy nimi i dzięki temu będą one wyśrodkowane
        buttonBar.add(Box.createGlue());
        buttonBar.add(Box.createGlue());
        buttonBar.add(przyciskKreatoraSklepu());
        add(buttonBar, BorderLayout.CENTER);

        // Rozpoczyna wątek roboczy, który tworzy obiekty 'JButton'
        loadimages.execute();

    }

    private JButton przyciskKreatoraSklepu() {
        final JButton przycisk = new JButton("Dodaj sklep");
        przycisk.addActionListener(new CreateShopButtonListener());
        return przycisk;
    }

    private static class CreateShopButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final ShopCreatorView view = new ShopCreatorView();
            final ShopCreatorModel model = new ShopCreatorModel();
            final ShopCreatorController kontroler = new ShopCreatorController(model, view);
            view.setKontroler(kontroler);
            view.setVisible(true);
        }
    }

    // Obiekt który wykonuje czasochłonne zadania
    // Oparte na kodzie : https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/IconDemoProject/src/components/IconDemoApp.java
    private SwingWorker<Void, OpenShopViewAction> loadimages = new SwingWorker<Void, OpenShopViewAction>() {
        @Override
        protected Void doInBackground() {

            for (Map.Entry<AbstractSklepView, String> entry : sklepyINazwyIkon.entrySet()){
                ImageIcon icon = new ImageIcon(pathToIcons + entry.getValue());
                icon = new ImageIcon(getScaledInstance(icon.getImage(), 50, 50));

                OpenShopViewAction action = new OpenShopViewAction(entry.getKey(), icon);
                publish(action);
            }
            return null;
        }

        @Override
        protected void process(java.util.List<OpenShopViewAction> chunks) {
            for (OpenShopViewAction thumbAction : chunks) {
                JButton actionButton = new JButton(thumbAction);
                buttonBar.add(actionButton, buttonBar.getComponentCount() - 1);
            }
        }

    };

    private static class OpenShopViewAction extends AbstractAction {

        OpenShopViewAction(AbstractSklepView view, Icon icon){
            putValue(Action.SMALL_ICON, icon);
            putValue("VIEW", view);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Otwiera widok sklepu
            ((AbstractSklepView)getValue("VIEW")).setVisible(true);
        }
    }

}
