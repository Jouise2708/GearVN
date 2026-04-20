package gearvn.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GearVNApp — PHẦN GIAO DIỆN (Frontend Only)
 *
 * Tất cả logic nghiệp vụ đã được tách ra:
 *   - Quản lý giỏ hàng   → CartService
 *   - Xác thực / Đăng nhập → AuthService
 *   - Đặt hàng / Thanh toán → OrderService
 *
 * Các chỗ cần backend tích hợp đều được đánh dấu "TODO [Backend]".
 */
public class GearVNApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    public GearVNApp() {
        setTitle("GearVN - App Mua Sắm");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(227, 28, 37));
        headerPanel.setPreferredSize(new Dimension(1200, 60));
        headerPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        JLabel logoLabel = new JLabel("GEARVN");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 26));
        logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeaderPanel.setOpaque(false);

        JButton accountBtn = new JButton("Đăng nhập / Đăng ký");
        accountBtn.setBackground(Color.WHITE);
        accountBtn.setForeground(new Color(227, 28, 37));
        accountBtn.setFocusPainted(false);
        accountBtn.setFont(new Font("Arial", Font.BOLD, 14));
        accountBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        accountBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "LOGIN"));

        JButton cartBtn = new JButton("Giỏ hàng");
        cartBtn.setBackground(Color.WHITE);
        cartBtn.setForeground(new Color(227, 28, 37));
        cartBtn.setFocusPainted(false);
        cartBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cartBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "CART"));

        rightHeaderPanel.add(accountBtn);
        rightHeaderPanel.add(cartBtn);
        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ── MAIN CONTENT (CardLayout) ────────────────────────────────────────
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(244, 244, 244));

        mainContentPanel.add(createHomePanel(), "HOME");
        mainContentPanel.add(createLoginPanel(), "LOGIN");
        mainContentPanel.add(createRegisterPanel(), "REGISTER");
        mainContentPanel.add(createForgotPasswordPanel(), "FORGOT_PASS");

        JPanel initialCart = createCartPanel();
        initialCart.setName("CART");
        mainContentPanel.add(initialCart, "CART");

        mainContentPanel.add(createProductListPanel("Laptop Gaming",
            new String[]{"Dưới 30 triệu", "30 - 50 triệu", "Trên 50 triệu"},
            new String[]{"Acer", "Asus", "MSI", "Lenovo"},
            "Laptop ASUS ROG Strix", "31.790.000đ"), "LAPTOP_LIST");

        mainContentPanel.add(createProductListPanel("Chuột Gaming",
            new String[]{"Dưới 1 triệu", "1 - 3 triệu", "Trên 3 triệu"},
            new String[]{"Razer", "Logitech", "Corsair"},
            "Chuột Razer DeathAdder", "1.590.000đ"), "CHUOT_LIST");

        mainContentPanel.add(createProductListPanel("Bàn Phím Gaming",
            new String[]{"Dưới 1 triệu", "1 - 3 triệu", "Trên 3 triệu"},
            new String[]{"AULA", "Corsair", "Akko", "Logitech"},
            "Bàn phím cơ AULA F75", "650.000đ"), "BAN_PHIM_LIST");

        mainContentPanel.add(createProductListPanel("Tai Nghe Gaming",
            new String[]{"Dưới 1 triệu", "1 - 3 triệu", "Trên 3 triệu"},
            new String[]{"Razer", "Corsair", "HyperX"},
            "Tai nghe Razer Barracuda", "2.890.000đ"), "TAI_NGHE_LIST");

        mainContentPanel.add(createCheckoutPanel(), "CHECKOUT");
        mainContentPanel.add(createPaymentPanel(),  "PAYMENT");
        mainContentPanel.add(createSuccessPanel(),  "SUCCESS");
        mainContentPanel.add(createAccountInfoPanel(),  "ACCOUNT_INFO");
        mainContentPanel.add(createOrderLookupPanel(),  "ORDER_LOOKUP");

        add(mainContentPanel, BorderLayout.CENTER);
        cardLayout.show(mainContentPanel, "HOME");
    }

    // =========================================================================
    // ĐIỀU HƯỚNG — NAVIGATION HELPERS
    // =========================================================================

    /** Mở màn hình chi tiết sản phẩm (UI only). */
    private void showProductDetail(String productName, String productPrice) {
        for (Component c : mainContentPanel.getComponents()) {
            if ("PRODUCT_DETAIL".equals(c.getName())) {
                mainContentPanel.remove(c);
                break;
            }
        }
        JPanel detailPanel = createProductDetailPanel(productName, productPrice);
        detailPanel.setName("PRODUCT_DETAIL");
        mainContentPanel.add(detailPanel, "PRODUCT_DETAIL");
        cardLayout.show(mainContentPanel, "PRODUCT_DETAIL");
    }

    /**
     * TODO [Backend]: Gọi CartService.addItem(productName, productPrice)
     * rồi gọi refreshCart() để cập nhật lại UI giỏ hàng.
     */
    private void addToCartAndShow(String productName, String productPrice) {
        // Sau khi CartService.addItem() thành công → refreshCart()
        cardLayout.show(mainContentPanel, "CART");
    }

    /**
     * TODO [Backend]: Sau khi CartService cập nhật giỏ hàng,
     * gọi hàm này để rebuild panel CART với dữ liệu mới.
     */
    private void refreshCart() {
        // Xây lại panel CART từ dữ liệu CartService trả về
    }

    /** Utility: xóa panel cũ theo key và gắn panel mới vào. */
    private void refreshPage(String key, JPanel newPanel) {
        for (Component c : mainContentPanel.getComponents()) {
            if (key.equals(c.getName())) { mainContentPanel.remove(c); break; }
        }
        newPanel.setName(key);
        mainContentPanel.add(newPanel, key);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // =========================================================================
    // MÀN HÌNH: GIỎ HÀNG
    // =========================================================================
    private JPanel createCartPanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(244, 244, 244));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(244, 244, 244));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Nút quay lại
        JLabel backLabel = new JLabel("<html><span style='font-size:14px; color:#555;'>&larr; <u>Mua thêm sản phẩm khác</u></span></html>");
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(244, 244, 244));
        backPanel.setMaximumSize(new Dimension(800, 40));
        backPanel.add(backLabel);
        contentPanel.add(backPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // ── MOCK UI: Hiển thị 1 sản phẩm mẫu để demo giao diện ─────────────
        // TODO [Backend]: Thay bằng dữ liệu thật từ CartService.getItems()
        // Nếu CartService trả về rỗng → hiển thị trạng thái "Giỏ hàng trống"
        // ────────────────────────────────────────────────────────────────────

        // --- Hộp giỏ hàng (mock) ---
        JPanel cartBox = new JPanel();
        cartBox.setLayout(new BoxLayout(cartBox, BoxLayout.Y_AXIS));
        cartBox.setBackground(Color.WHITE);
        cartBox.setMaximumSize(new Dimension(800, 5000));
        cartBox.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

        // Header tổng tiền
        JPanel totalHeader = new JPanel(new BorderLayout());
        totalHeader.setBackground(new Color(255, 240, 242));
        totalHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel totalText = new JLabel("Tổng tiền:");
        totalText.setFont(new Font("Arial", Font.BOLD, 18));
        // TODO [Backend]: CartService.getFormattedTotal()
        JLabel totalPriceLabel = new JLabel("31.790.000đ");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalPriceLabel.setForeground(new Color(227, 28, 37));
        totalHeader.add(totalText, BorderLayout.WEST);
        totalHeader.add(totalPriceLabel, BorderLayout.EAST);
        cartBox.add(totalHeader);

        // Mock item row
        JPanel itemRow = new JPanel(new GridBagLayout());
        itemRow.setBackground(Color.WHITE);
        itemRow.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);

        JPanel imgMock = new JPanel(new BorderLayout());
        imgMock.setPreferredSize(new Dimension(100, 80));
        imgMock.setBackground(new Color(230, 230, 230));
        imgMock.add(new JLabel("Image", SwingConstants.CENTER), BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        itemRow.add(imgMock, gbc);

        JLabel nameLabel = new JLabel("<html><div style='width:200px; font-weight:bold;'>Laptop ASUS ROG Strix V1</div></html>");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.weightx = 0.5;
        itemRow.add(nameLabel, gbc);

        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setBackground(Color.WHITE);
        JLabel pLbl = new JLabel("31.790.000đ");
        pLbl.setFont(new Font("Arial", Font.BOLD, 16));
        pLbl.setForeground(new Color(227, 28, 37));
        pricePanel.add(pLbl);
        gbc.gridx = 2; gbc.weightx = 0.2;
        itemRow.add(pricePanel, gbc);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(Color.WHITE);

        JLabel deleteBtn = new JLabel("<html><b style='color:gray;'>✕</b></html>");
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        // TODO [Backend]: CartService.removeItem(itemId) → refreshCart()
        deleteBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // CartService.removeItem(...) rồi refreshCart()
            }
        });

        JLabel qtyLabel = new JLabel(" 1 ", SwingConstants.CENTER);
        qtyLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        qtyPanel.setBackground(Color.WHITE);
        JButton minusBtn = new JButton("-");
        JButton plusBtn  = new JButton("+");
        // TODO [Backend]: CartService.decreaseQty(itemId) / increaseQty(itemId) → refreshCart()
        minusBtn.addActionListener(ev -> { /* CartService.decreaseQty(...) */ });
        plusBtn.addActionListener(ev ->  { /* CartService.increaseQty(...) */ });
        qtyPanel.add(minusBtn);
        qtyPanel.add(qtyLabel);
        qtyPanel.add(plusBtn);

        actionPanel.add(deleteBtn, BorderLayout.NORTH);
        actionPanel.add(qtyPanel,  BorderLayout.SOUTH);
        gbc.gridx = 3; gbc.weightx = 0.1;
        itemRow.add(actionPanel, gbc);

        cartBox.add(itemRow);

        // Nút Đặt hàng ngay
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 0, 30, 0));
        JButton checkoutBtn = new JButton("ĐẶT HÀNG NGAY");
        checkoutBtn.setBackground(new Color(227, 28, 37));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 18));
        checkoutBtn.setPreferredSize(new Dimension(350, 50));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutBtn.setBorder(new RoundedBorder(8, new Color(227, 28, 37)));
        checkoutBtn.addActionListener(e -> {
            refreshPage("CHECKOUT", createCheckoutPanel());
            cardLayout.show(mainContentPanel, "CHECKOUT");
        });
        footerPanel.add(checkoutBtn);
        cartBox.add(footerPanel);
        contentPanel.add(cartBox);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }

    // =========================================================================
    // BƯỚC 1 THANH TOÁN — THÔNG TIN ĐẶT HÀNG
    // =========================================================================
    private JPanel createCheckoutPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(244, 244, 244));

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(new Color(244, 244, 244));
        scroll.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel backLbl = new JLabel("← Trở về");
        backLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        backLbl.setForeground(new Color(80, 80, 80));
        backLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "CART"); }
        });
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setBackground(new Color(244, 244, 244)); backRow.add(backLbl);
        backRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.add(backRow);
        scroll.add(Box.createRigidArea(new Dimension(0, 10)));
        scroll.add(createStepBar(2));
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 30, 30, 30)));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sectionTitle = new JLabel("Thông tin khách mua hàng");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(sectionTitle);
        box.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel genderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderRow.setBackground(Color.WHITE);
        genderRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonGroup genderGrp = new ButtonGroup();
        JRadioButton rAnh = new JRadioButton("Anh"); rAnh.setBackground(Color.WHITE); rAnh.setSelected(true);
        JRadioButton rChi = new JRadioButton("Chị"); rChi.setBackground(Color.WHITE);
        genderGrp.add(rAnh); genderGrp.add(rChi);
        genderRow.add(rAnh); genderRow.add(Box.createHorizontalStrut(20)); genderRow.add(rChi);
        box.add(genderRow);
        box.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel namePhoneRow = new JPanel(new GridLayout(1, 2, 15, 0));
        namePhoneRow.setBackground(Color.WHITE);
        namePhoneRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        namePhoneRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel nameCol = new JPanel(new BorderLayout(0, 5));
        nameCol.setBackground(Color.WHITE);
        nameCol.add(new JLabel("Nhập họ tên:"), BorderLayout.NORTH);
        JTextField nameField = createStyledTextField("Họ và tên");
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameCol.add(nameField, BorderLayout.CENTER);

        JPanel phoneCol = new JPanel(new BorderLayout(0, 5));
        phoneCol.setBackground(Color.WHITE);
        phoneCol.add(new JLabel("Nhập số điện thoại:"), BorderLayout.NORTH);
        JTextField phoneField = createStyledTextField("Số điện thoại");
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        phoneCol.add(phoneField, BorderLayout.CENTER);

        namePhoneRow.add(nameCol); namePhoneRow.add(phoneCol);
        box.add(namePhoneRow);
        box.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel deliveryTitle = new JLabel("Chọn cách nhận hàng");
        deliveryTitle.setFont(new Font("Arial", Font.BOLD, 15));
        deliveryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(deliveryTitle);
        box.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel deliveryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        deliveryRow.setBackground(Color.WHITE);
        deliveryRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JRadioButton rShip  = new JRadioButton("Giao hàng tận nơi");  rShip.setBackground(Color.WHITE); rShip.setSelected(true);
        JRadioButton rStore = new JRadioButton("Nhận tại cửa hàng"); rStore.setBackground(Color.WHITE);
        ButtonGroup deliveryGrp = new ButtonGroup();
        deliveryGrp.add(rShip); deliveryGrp.add(rStore);
        deliveryRow.add(rShip); deliveryRow.add(Box.createHorizontalStrut(20)); deliveryRow.add(rStore);
        box.add(deliveryRow);
        box.add(Box.createRigidArea(new Dimension(0, 12)));

        JTextField addrField = createStyledTextField("Số nhà, tên đường");
        addrField.setAlignmentX(Component.LEFT_ALIGNMENT);
        addrField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        box.add(addrField);
        box.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel cityRow = new JPanel(new GridLayout(1, 2, 15, 0));
        cityRow.setBackground(Color.WHITE);
        cityRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        String[] phuongs = {"Chọn phường, xã", "Phường Tân Hưng", "Phường Bình Thuận", "Phường 1"};
        String[] tinhs   = {"Chọn tỉnh, thành phố", "Hồ Chí Minh", "Hà Nội", "Đà Nẵng"};
        JComboBox<String> cbPhuong = new JComboBox<>(phuongs);
        JComboBox<String> cbTinh   = new JComboBox<>(tinhs);
        cbPhuong.setFont(new Font("Arial", Font.PLAIN, 13));
        cbTinh.setFont(new Font("Arial", Font.PLAIN, 13));
        cityRow.add(cbPhuong); cityRow.add(cbTinh);
        box.add(cityRow);
        box.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel shipTitle = new JLabel("Dịch vụ giao hàng");
        shipTitle.setFont(new Font("Arial", Font.BOLD, 15));
        shipTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(shipTitle);
        box.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel shipRow = new JPanel(new BorderLayout());
        shipRow.setBackground(new Color(245, 245, 245));
        shipRow.setBorder(new EmptyBorder(10, 12, 10, 12));
        shipRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        shipRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JRadioButton rFreeShip = new JRadioButton("Miễn phí vận chuyển");
        rFreeShip.setBackground(new Color(245, 245, 245)); rFreeShip.setSelected(true);
        JLabel shipCost = new JLabel("0đ");
        shipCost.setFont(new Font("Arial", Font.BOLD, 14));
        shipRow.add(rFreeShip, BorderLayout.WEST); shipRow.add(shipCost, BorderLayout.EAST);
        box.add(shipRow);
        box.add(Box.createRigidArea(new Dimension(0, 20)));

        JSeparator sep = new JSeparator();
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(sep);
        box.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(Color.WHITE);
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel totalLbl = new JLabel("Tổng tiền :");
        totalLbl.setFont(new Font("Arial", Font.BOLD, 16));
        // TODO [Backend]: CartService.getFormattedTotal()
        JLabel totalVal = new JLabel("0đ");
        totalVal.setFont(new Font("Arial", Font.BOLD, 20));
        totalVal.setForeground(new Color(227, 28, 37));
        totalRow.add(totalLbl, BorderLayout.WEST); totalRow.add(totalVal, BorderLayout.EAST);
        box.add(totalRow);
        box.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton orderBtn = new JButton("ĐẶT HÀNG NGAY");
        orderBtn.setBackground(new Color(227, 28, 37));
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setFont(new Font("Arial", Font.BOLD, 16));
        orderBtn.setFocusPainted(false);
        orderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        orderBtn.setBorder(new RoundedBorder(6, new Color(227, 28, 37)));
        orderBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        orderBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // TODO [Backend]: Validate input → OrderService.createOrder(name, phone, addr, ...) → navigate
        orderBtn.addActionListener(e -> {
            refreshPage("PAYMENT", createPaymentPanel());
            cardLayout.show(mainContentPanel, "PAYMENT");
        });
        box.add(orderBtn);

        scroll.add(box);
        scroll.add(Box.createRigidArea(new Dimension(0, 30)));

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    // =========================================================================
    // BƯỚC 2 THANH TOÁN — PHƯƠNG THỨC THANH TOÁN
    // =========================================================================
    private JPanel createPaymentPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(244, 244, 244));

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(new Color(244, 244, 244));
        scroll.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel backLbl = new JLabel("← Trở về");
        backLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        backLbl.setForeground(new Color(80, 80, 80));
        backLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "CHECKOUT"); }
        });
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setBackground(new Color(244, 244, 244)); backRow.add(backLbl);
        backRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.add(backRow);
        scroll.add(Box.createRigidArea(new Dimension(0, 10)));
        scroll.add(createStepBar(3));
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 30, 25, 30)));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel orderInfoTitle = new JLabel("Thông tin đặt hàng");
        orderInfoTitle.setFont(new Font("Arial", Font.BOLD, 16));
        orderInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(orderInfoTitle);
        box.add(Box.createRigidArea(new Dimension(0, 15)));

        // TODO [Backend]: Lấy từ OrderService.getCurrentDraft()
        String[][] infoRows = {
            {"Khách hàng:",        "Nguyễn Văn A"},
            {"Số điện thoại:",     "0123456789"},
            {"Địa chỉ nhận hàng:", "19 Nguyễn Hữu Thọ, Phường Tân Hưng, Hồ Chí Minh"},
            {"Tạm tính:",          "31.790.000đ"},
            {"Phí vận chuyển:",    "Miễn phí"},
            {"Tổng tiền:",         "31.790.000đ"}
        };
        for (String[] row : infoRows) {
            JPanel infoRow = new JPanel(new BorderLayout(10, 0));
            infoRow.setBackground(Color.WHITE);
            infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            infoRow.setBorder(new EmptyBorder(3, 0, 3, 0));
            JLabel keyLbl = new JLabel("● " + row[0]);
            keyLbl.setFont(new Font("Arial", Font.PLAIN, 14));
            keyLbl.setPreferredSize(new Dimension(200, 28));
            JLabel valLbl = new JLabel(row[1]);
            valLbl.setFont(new Font("Arial", Font.PLAIN, 14));
            boolean isPrice = row[1].endsWith("đ") && !row[1].equals("Miễn phí");
            if (isPrice) valLbl.setForeground(new Color(227, 28, 37));
            infoRow.add(keyLbl, BorderLayout.WEST);
            infoRow.add(valLbl, BorderLayout.CENTER);
            box.add(infoRow);
        }

        box.add(Box.createRigidArea(new Dimension(0, 18)));
        JSeparator sep = new JSeparator(); sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(sep);
        box.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel payTitle = new JLabel("Chọn phương thức thanh toán");
        payTitle.setFont(new Font("Arial", Font.BOLD, 15));
        payTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(payTitle);
        box.add(Box.createRigidArea(new Dimension(0, 10)));

        JRadioButton rCOD  = new JRadioButton("Thanh toán khi giao hàng (COD)"); rCOD.setBackground(Color.WHITE); rCOD.setSelected(true);
        JRadioButton rBank = new JRadioButton("Chuyển khoản ngân hàng");          rBank.setBackground(Color.WHITE);
        JRadioButton rMomo = new JRadioButton("Ví MoMo");                          rMomo.setBackground(Color.WHITE);
        rCOD.setFont(new Font("Arial", Font.PLAIN, 14));  rCOD.setAlignmentX(Component.LEFT_ALIGNMENT);
        rBank.setFont(new Font("Arial", Font.PLAIN, 14)); rBank.setAlignmentX(Component.LEFT_ALIGNMENT);
        rMomo.setFont(new Font("Arial", Font.PLAIN, 14)); rMomo.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonGroup payGrp = new ButtonGroup();
        payGrp.add(rCOD); payGrp.add(rBank); payGrp.add(rMomo);
        box.add(rCOD);  box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(rBank); box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(rMomo); box.add(Box.createRigidArea(new Dimension(0, 22)));

        JButton confirmBtn = new JButton("ĐẶT HÀNG NGAY");
        confirmBtn.setBackground(new Color(227, 28, 37));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 16));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setBorder(new RoundedBorder(6, new Color(227, 28, 37)));
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // TODO [Backend]: OrderService.confirmPayment(paymentMethod) → CartService.clear() → navigate
        confirmBtn.addActionListener(e -> {
            refreshPage("SUCCESS", createSuccessPanel());
            cardLayout.show(mainContentPanel, "SUCCESS");
        });
        box.add(confirmBtn);

        scroll.add(box);
        scroll.add(Box.createRigidArea(new Dimension(0, 30)));
        scroll.add(createCheckoutFooter());

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    // =========================================================================
    // BƯỚC 3 — ĐẶT HÀNG THÀNH CÔNG
    // =========================================================================
    private JPanel createSuccessPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(244, 244, 244));

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(new Color(244, 244, 244));
        scroll.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel backLbl = new JLabel("← Trở về");
        backLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        backLbl.setForeground(new Color(80, 80, 80));
        backLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setBackground(new Color(244, 244, 244)); backRow.add(backLbl);
        backRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.add(backRow);
        scroll.add(Box.createRigidArea(new Dimension(0, 10)));
        scroll.add(createStepBar(4));
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(35, 40, 35, 40)));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton successBanner = new JButton("Đặt hàng thành công");
        successBanner.setBackground(new Color(227, 28, 37));
        successBanner.setForeground(Color.WHITE);
        successBanner.setFont(new Font("Arial", Font.BOLD, 20));
        successBanner.setFocusPainted(false);
        successBanner.setBorder(new RoundedBorder(6, new Color(227, 28, 37)));
        successBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        successBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        successBanner.setEnabled(false);
        box.add(successBanner);
        box.add(Box.createRigidArea(new Dimension(0, 25)));

        JTextArea thankMsg = new JTextArea(
            "Cảm ơn Quý Khách đã cho GearVN có cơ hội được phục vụ.\n" +
            "Nhân viên GearVN sẽ liên hệ với quý khách sớm nhất có thể."
        );
        thankMsg.setWrapStyleWord(true); thankMsg.setLineWrap(true);
        thankMsg.setEditable(false); thankMsg.setFocusable(false);
        thankMsg.setBackground(Color.WHITE);
        thankMsg.setFont(new Font("Arial", Font.PLAIN, 15));
        thankMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(thankMsg);
        box.add(Box.createRigidArea(new Dimension(0, 15)));

        // TODO [Backend]: OrderService.getLastOrderCode()
        JLabel orderCodeLbl = new JLabel("Mã đơn hàng của bạn là: #GVN12345");
        orderCodeLbl.setFont(new Font("Arial", Font.BOLD, 16));
        orderCodeLbl.setForeground(new Color(227, 28, 37));
        orderCodeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(orderCodeLbl);
        box.add(Box.createRigidArea(new Dimension(0, 30)));

        JSeparator sep = new JSeparator(); sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(sep);
        box.add(Box.createRigidArea(new Dimension(0, 20)));

        scroll.add(box);
        scroll.add(Box.createRigidArea(new Dimension(0, 20)));
        scroll.add(createCheckoutFooter());

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    // =========================================================================
    // UI HELPER — THANH BƯỚC TIẾN (Step Bar)
    // =========================================================================
    private JPanel createStepBar(int activeStep) {
        String[] labels = {"Giỏ Hàng", "Thông tin\nđặt hàng", "Thanh toán", "Hoàn tất"};
        JPanel bar = new JPanel(new GridLayout(1, labels.length));
        bar.setBackground(Color.WHITE);
        bar.setBorder(new EmptyBorder(15, 10, 15, 10));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        Color RED  = new Color(227, 28, 37);
        Color GREY = new Color(180, 180, 180);

        for (int i = 0; i < labels.length; i++) {
            int   step   = i + 1;
            boolean done   = step < activeStep;
            boolean active = step == activeStep;
            Color circ = (done || active) ? RED : GREY;

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(Color.WHITE);
            GridBagConstraints g = new GridBagConstraints();

            JLabel circle = new JLabel(String.valueOf(step), SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics gr) {
                    Graphics2D g2 = (Graphics2D) gr.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(circ);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(gr);
                }
            };
            circle.setForeground(Color.WHITE);
            circle.setFont(new Font("Arial", Font.BOLD, 13));
            circle.setOpaque(false);
            circle.setPreferredSize(new Dimension(28, 28));

            JLabel lbl = new JLabel("<html><center>" + labels[i].replace("\n", "<br>") + "</center></html>",
                SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", active ? Font.BOLD : Font.PLAIN, 12));
            lbl.setForeground(active ? RED : (done ? new Color(60, 60, 60) : GREY));

            g.gridx = 0; g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
            cell.add(circle, g);
            g.gridy = 1; g.insets = new Insets(0, 0, 0, 0);
            cell.add(lbl, g);
            bar.add(cell);
        }
        return bar;
    }

    // =========================================================================
    // UI HELPER — FOOTER THANH TOÁN
    // =========================================================================
    private JPanel createCheckoutFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(51, 51, 51));
        footer.setBorder(new EmptyBorder(20, 30, 20, 30));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel cols = new JPanel(new GridLayout(1, 3, 20, 0));
        cols.setBackground(new Color(51, 51, 51));

        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setBackground(new Color(51, 51, 51));
        JLabel logoF = new JLabel("GEARVN");
        logoF.setForeground(Color.WHITE); logoF.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel siteF = new JLabel("GEARVN.COM");
        siteF.setForeground(new Color(180, 180, 180)); siteF.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel aboutTitle = new JLabel("VỀ GEARVN"); aboutTitle.setForeground(Color.WHITE); aboutTitle.setFont(new Font("Arial", Font.BOLD, 13));
        col1.add(logoF); col1.add(siteF); col1.add(Box.createRigidArea(new Dimension(0, 10)));
        col1.add(aboutTitle);
        for (String s : new String[]{"Giới thiệu", "Tuyển dụng", "Liên hệ"}) {
            JLabel l = new JLabel(s); l.setForeground(new Color(180, 180, 180)); l.setFont(new Font("Arial", Font.PLAIN, 12));
            col1.add(l);
        }

        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setBackground(new Color(51, 51, 51));
        JLabel policyTitle = new JLabel("CHÍNH SÁCH"); policyTitle.setForeground(Color.WHITE); policyTitle.setFont(new Font("Arial", Font.BOLD, 13));
        col2.add(policyTitle);
        for (String s : new String[]{"Chính sách bảo hành", "Chính sách thanh toán", "Chính sách giao hàng", "Chính sách bảo mật"}) {
            JLabel l = new JLabel(s); l.setForeground(new Color(180, 180, 180)); l.setFont(new Font("Arial", Font.PLAIN, 12));
            col2.add(l); col2.add(Box.createRigidArea(new Dimension(0, 3)));
        }

        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.setBackground(new Color(51, 51, 51));
        JLabel hotlineTitle = new JLabel("TỔNG ĐÀI HỖ TRỢ (8:00 - 21:00)");
        hotlineTitle.setForeground(Color.WHITE); hotlineTitle.setFont(new Font("Arial", Font.BOLD, 12));
        col3.add(hotlineTitle); col3.add(Box.createRigidArea(new Dimension(0, 5)));
        for (String s : new String[]{"Mua hàng: 1900.5301", "Bảo hành: 1900.5325", "Khiếu nại: 1800.6173", "Email: cskh@gearvn.com"}) {
            JLabel l = new JLabel(s); l.setForeground(new Color(180, 180, 180)); l.setFont(new Font("Arial", Font.PLAIN, 12));
            col3.add(l); col3.add(Box.createRigidArea(new Dimension(0, 3)));
        }

        cols.add(col1); cols.add(col2); cols.add(col3);
        footer.add(cols, BorderLayout.CENTER);

        JLabel social = new JLabel("KẾT NỐI VỚI CHÚNG TÔI  📘 📷 🎬 🎵");
        social.setForeground(new Color(180, 180, 180));
        social.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.add(social, BorderLayout.SOUTH);
        return footer;
    }

    // =========================================================================
    // MÀN HÌNH TÀI KHOẢN — SIDEBAR DÙNG CHUNG
    // =========================================================================
    private JPanel createAccountSidebar(String activeKey) {
        Color RED = new Color(227, 28, 37);
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 0, 25, 0)));

        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        avatarRow.setBackground(Color.WHITE);
        avatarRow.setMaximumSize(new Dimension(210, 55));

        JLabel avatarIcon = new JLabel("👤") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarIcon.setPreferredSize(new Dimension(40, 40));
        avatarIcon.setHorizontalAlignment(SwingConstants.CENTER);
        avatarIcon.setOpaque(false);

        // TODO [Backend]: AuthService.getCurrentUser().getDisplayName()
        JLabel nameLbl = new JLabel("UserName");
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));

        avatarRow.add(avatarIcon);
        avatarRow.add(nameLbl);
        sidebar.add(avatarRow);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        String[][] menuItems = {
            {"👤  Thông tin tài khoản", "ACCOUNT_INFO"},
            {"🛒  Tra cứu đơn hàng",    "ORDER_LOOKUP"},
            {"🚪  Đăng xuất",           "LOGOUT"}
        };
        for (String[] item : menuItems) {
            boolean isActive = item[1].equals(activeKey);
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(210, 44));
            row.setCursor(new Cursor(Cursor.HAND_CURSOR));
            row.setBorder(isActive
                ? BorderFactory.createCompoundBorder(new MatteBorder(0, 3, 0, 0, RED), new EmptyBorder(8, 15, 8, 8))
                : new EmptyBorder(8, 18, 8, 8));

            JLabel lbl = new JLabel(item[0]);
            lbl.setFont(new Font("Arial", isActive ? Font.BOLD : Font.PLAIN, 14));
            lbl.setForeground(isActive ? RED : Color.DARK_GRAY);

            JLabel arrow = new JLabel("→");
            arrow.setForeground(isActive ? RED : Color.LIGHT_GRAY);
            arrow.setFont(new Font("Arial", Font.PLAIN, 14));

            row.add(lbl, BorderLayout.WEST);
            row.add(arrow, BorderLayout.EAST);

            String navKey = item[1];
            row.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if ("LOGOUT".equals(navKey)) {
                        // TODO [Backend]: AuthService.logout()
                        int c = JOptionPane.showConfirmDialog(GearVNApp.this,
                            "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
                        if (c == JOptionPane.YES_OPTION) {
                            cardLayout.show(mainContentPanel, "HOME");
                        }
                    } else {
                        refreshPage(navKey, navKey.equals("ACCOUNT_INFO")
                            ? createAccountInfoPanel() : createOrderLookupPanel());
                        cardLayout.show(mainContentPanel, navKey);
                    }
                }
                public void mouseEntered(MouseEvent e) { row.setBackground(new Color(255, 245, 245)); }
                public void mouseExited(MouseEvent e)  { row.setBackground(Color.WHITE); }
            });
            sidebar.add(row);
        }
        return sidebar;
    }

    // =========================================================================
    // MÀN HÌNH: THÔNG TIN TÀI KHOẢN
    // =========================================================================
    private JPanel createAccountInfoPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(244, 244, 244));

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(new Color(244, 244, 244));
        body.setBorder(new EmptyBorder(20, 25, 20, 25));
        body.add(createAccountSidebar("ACCOUNT_INFO"), BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 35, 35, 35)));

        JLabel title = new JLabel("Thông tin tài khoản");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 22)));

        content.add(makeFormLabel("Họ và tên:"));
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        // TODO [Backend]: Điền giá trị từ UserService.getCurrentUser()
        JTextField nameField = createStyledTextField("Họ và tên");
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        content.add(nameField);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        content.add(makeFormLabel("Giới tính:"));
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        JPanel genderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderRow.setBackground(Color.WHITE);
        genderRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonGroup gGrp = new ButtonGroup();
        JRadioButton rNam = new JRadioButton("Nam"); rNam.setBackground(Color.WHITE); rNam.setSelected(true);
        JRadioButton rNu  = new JRadioButton("Nữ");  rNu.setBackground(Color.WHITE);
        gGrp.add(rNam); gGrp.add(rNu);
        genderRow.add(rNam); genderRow.add(Box.createHorizontalStrut(20)); genderRow.add(rNu);
        content.add(genderRow);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        content.add(makeFormLabel("Số điện thoại:"));
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        JTextField phoneField = createStyledTextField("Số điện thoại");
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        content.add(phoneField);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        content.add(makeFormLabel("Địa chỉ:"));
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        JTextField addrField = createStyledTextField("Số nhà, tên đường");
        addrField.setAlignmentX(Component.LEFT_ALIGNMENT);
        addrField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        content.add(addrField);
        content.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel cityRow = new JPanel(new GridLayout(1, 2, 12, 0));
        cityRow.setBackground(Color.WHITE);
        cityRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        String[] phuongs = {"Chọn phường, xã", "Phường Tân Hưng", "Phường Bình Thuận", "Phường 1"};
        String[] tinhs   = {"Chọn tỉnh, thành phố", "Hồ Chí Minh", "Hà Nội", "Đà Nẵng"};
        JComboBox<String> cbPhuong = new JComboBox<>(phuongs);
        JComboBox<String> cbTinh   = new JComboBox<>(tinhs);
        cbPhuong.setFont(new Font("Arial", Font.PLAIN, 13));
        cbTinh.setFont(new Font("Arial", Font.PLAIN, 13));
        cityRow.add(cbPhuong); cityRow.add(cbTinh);
        content.add(cityRow);
        content.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton saveBtn = new JButton("LƯU THAY ĐỔI");
        saveBtn.setBackground(new Color(227, 28, 37));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 15));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setBorder(new RoundedBorder(6, new Color(227, 28, 37)));
        saveBtn.setMaximumSize(new Dimension(200, 44));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // TODO [Backend]: UserService.updateProfile(name, phone, address, ...)
        saveBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(GearVNApp.this,
                "Đã lưu thông tin tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE)
        );
        content.add(saveBtn);

        body.add(content, BorderLayout.CENTER);

        JPanel scrollWrap = new JPanel(new BorderLayout());
        scrollWrap.setBackground(new Color(244, 244, 244));
        scrollWrap.add(body, BorderLayout.CENTER);
        scrollWrap.add(createCheckoutFooter(), BorderLayout.SOUTH);

        JScrollPane sp = new JScrollPane(scrollWrap);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    // =========================================================================
    // MÀN HÌNH: TRA CỨU ĐƠN HÀNG
    // =========================================================================
    private JPanel createOrderLookupPanel() {
        Color RED = new Color(227, 28, 37);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(244, 244, 244));

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(new Color(244, 244, 244));
        body.setBorder(new EmptyBorder(20, 25, 20, 25));
        body.add(createAccountSidebar("ORDER_LOOKUP"), BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 30, 30, 30)));

        JLabel title = new JLabel("Quản lý đơn hàng");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        // Tab bar
        String[] tabNames = {"TẤT CẢ", "ĐANG XỬ LÝ", "ĐANG VẬN CHUYỂN", "HOÀN THÀNH", "HỦY"};
        JPanel tabBar = new JPanel(new GridLayout(1, tabNames.length, 0, 0));
        tabBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tabBar.setBorder(new MatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
        for (int i = 0; i < tabNames.length; i++) {
            boolean active = (i == 0);
            JLabel tab = new JLabel(tabNames[i], SwingConstants.CENTER);
            tab.setFont(new Font("Arial", active ? Font.BOLD : Font.PLAIN, 12));
            tab.setForeground(active ? RED : new Color(100, 100, 100));
            tab.setOpaque(true);
            tab.setBackground(Color.WHITE);
            tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tab.setBorder(active ? new MatteBorder(0, 0, 2, 0, RED) : new EmptyBorder(0, 0, 2, 0));
            tab.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (!tab.getForeground().equals(RED)) tab.setForeground(new Color(80, 80, 80)); }
                public void mouseExited(MouseEvent e)  { if (!tab.getForeground().equals(RED)) tab.setForeground(new Color(100, 100, 100)); }
            });
            tabBar.add(tab);
        }
        content.add(tabBar);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        // Thanh tìm kiếm
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextField searchField = new JTextField("Tìm kiếm theo tên, số đơn hàng...");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(4, 10, 4, 10)));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().startsWith("Tìm kiếm")) { searchField.setText(""); searchField.setForeground(Color.BLACK); }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) { searchField.setText("Tìm kiếm theo tên, số đơn hàng..."); searchField.setForeground(Color.GRAY); }
            }
        });
        JButton searchBtn = new JButton("🔍");
        searchBtn.setBackground(RED);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);
        content.add(searchRow);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        // TODO [Backend]: OrderService.getOrdersByUser() → hiển thị danh sách đơn hàng
        // Hiện tại hiển thị trạng thái "Chưa có đơn hàng"
        JPanel emptyState = new JPanel(new GridBagLayout());
        emptyState.setBackground(Color.WHITE);
        emptyState.setAlignmentX(Component.LEFT_ALIGNMENT);
        emptyState.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel emptyInner = new JPanel();
        emptyInner.setLayout(new BoxLayout(emptyInner, BoxLayout.Y_AXIS));
        emptyInner.setBackground(Color.WHITE);
        JLabel emptyIcon = new JLabel("🛒", SwingConstants.CENTER);
        emptyIcon.setFont(new Font("Arial", Font.PLAIN, 38));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel emptyMsg = new JLabel("Bạn chưa đặt hàng sản phẩm nào");
        emptyMsg.setFont(new Font("Arial", Font.PLAIN, 15));
        emptyMsg.setForeground(new Color(130, 130, 130));
        emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton shopNowBtn = new JButton("Mua sắm ngay");
        shopNowBtn.setBackground(RED);
        shopNowBtn.setForeground(Color.WHITE);
        shopNowBtn.setFont(new Font("Arial", Font.BOLD, 13));
        shopNowBtn.setFocusPainted(false);
        shopNowBtn.setBorder(new RoundedBorder(6, RED));
        shopNowBtn.setPreferredSize(new Dimension(160, 38));
        shopNowBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopNowBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        shopNowBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));
        emptyInner.add(emptyIcon);
        emptyInner.add(Box.createRigidArea(new Dimension(0, 10)));
        emptyInner.add(emptyMsg);
        emptyInner.add(Box.createRigidArea(new Dimension(0, 16)));
        emptyInner.add(shopNowBtn);
        emptyState.add(emptyInner);
        content.add(emptyState);

        body.add(content, BorderLayout.CENTER);

        JPanel scrollWrap = new JPanel(new BorderLayout());
        scrollWrap.setBackground(new Color(244, 244, 244));
        scrollWrap.add(body, BorderLayout.CENTER);
        scrollWrap.add(createCheckoutFooter(), BorderLayout.SOUTH);

        JScrollPane sp = new JScrollPane(scrollWrap);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel makeFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // =========================================================================
    // MÀN HÌNH: TRANG CHỦ
    // =========================================================================
    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout(15, 0));
        homePanel.setBackground(new Color(244, 244, 244));
        homePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new LineBorder(new Color(220, 220, 220)));

        String[] categories = {"Laptop Gaming", "Chuột Gaming", "Bàn Phím Gaming", "Tai Nghe Gaming", "Màn Hình", "Linh Kiện PC"};
        String[] linkKeys   = {"LAPTOP_LIST", "CHUOT_LIST", "BAN_PHIM_LIST", "TAI_NGHE_LIST", "HOME", "HOME"};

        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            String key = linkKeys[i];
            JLabel catLabel = new JLabel(cat);
            catLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            catLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
            catLabel.setMaximumSize(new Dimension(200, 50));
            catLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            catLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, key); }
                public void mouseEntered(MouseEvent e) { catLabel.setForeground(Color.RED); }
                public void mouseExited(MouseEvent e)  { catLabel.setForeground(Color.BLACK); }
            });
            sidebar.add(catLabel);
            sidebar.add(new JSeparator());
        }
        homePanel.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(244, 244, 244));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Color.DARK_GRAY);
        banner.setPreferredSize(new Dimension(800, 250));
        banner.setMaximumSize(new Dimension(2000, 250));
        JLabel bannerText = new JLabel("BANNER QUẢNG CÁO GVN", SwingConstants.CENTER);
        bannerText.setForeground(Color.WHITE);
        bannerText.setFont(new Font("Arial", Font.BOLD, 24));
        banner.add(bannerText, BorderLayout.CENTER);
        contentPanel.add(banner);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // TODO [Backend]: ProductService.getBestSellers("laptop") / ("mouse") / ("keyboard")
        contentPanel.add(createProductSection("Laptop Gaming Bán Chạy", "Laptop ASUS ROG Strix", "31.790.000đ"));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(createProductSection("Chuột Gaming Bán Chạy", "Chuột Razer DeathAdder", "1.590.000đ"));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(createProductSection("Bàn Phím Gaming Bán Chạy", "Bàn phím AKKO 3098", "1.290.000đ"));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        homePanel.add(scrollPane, BorderLayout.CENTER);
        return homePanel;
    }

    // =========================================================================
    // MÀN HÌNH: DANH SÁCH SẢN PHẨM
    // =========================================================================
    private JPanel createProductListPanel(String categoryName, String[] priceRanges, String[] brands, String mockName, String mockPrice) {
        JPanel listPanel = new JPanel(new BorderLayout(15, 10));
        listPanel.setBackground(new Color(244, 244, 244));
        listPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JLabel breadcrumb = new JLabel("<html><a href=''>Trang chủ</a> > <b>" + categoryName + "</b></html>");
        breadcrumb.setFont(new Font("Arial", Font.PLAIN, 14));
        breadcrumb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breadcrumb.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        listPanel.add(breadcrumb, BorderLayout.NORTH);

        JPanel filterSidebar = new JPanel();
        filterSidebar.setLayout(new BoxLayout(filterSidebar, BoxLayout.Y_AXIS));
        filterSidebar.setBackground(Color.WHITE);
        filterSidebar.setPreferredSize(new Dimension(220, 0));
        filterSidebar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)));

        JLabel priceFilterLabel = new JLabel("Khoảng giá");
        priceFilterLabel.setFont(new Font("Arial", Font.BOLD, 15));
        filterSidebar.add(priceFilterLabel);
        filterSidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        for (String price : priceRanges) {
            JCheckBox cb = new JCheckBox(price);
            cb.setBackground(Color.WHITE);
            cb.setFont(new Font("Arial", Font.PLAIN, 13));
            filterSidebar.add(cb);
            filterSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        filterSidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel brandLabel = new JLabel("Thương hiệu");
        brandLabel.setFont(new Font("Arial", Font.BOLD, 15));
        filterSidebar.add(brandLabel);
        filterSidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        for (String brand : brands) {
            JCheckBox cb = new JCheckBox(brand);
            cb.setBackground(Color.WHITE);
            cb.setFont(new Font("Arial", Font.PLAIN, 13));
            filterSidebar.add(cb);
            filterSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        listPanel.add(filterSidebar, BorderLayout.WEST);

        JPanel rightContent = new JPanel();
        rightContent.setLayout(new BoxLayout(rightContent, BoxLayout.Y_AXIS));
        rightContent.setBackground(new Color(244, 244, 244));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(0, 51, 204));
        banner.setPreferredSize(new Dimension(800, 200));
        banner.setMaximumSize(new Dimension(2000, 200));
        JLabel bannerText = new JLabel("GEAR ARENA - " + categoryName.toUpperCase(), SwingConstants.CENTER);
        bannerText.setForeground(Color.YELLOW);
        bannerText.setFont(new Font("Arial", Font.BOLD, 28));
        banner.add(bannerText, BorderLayout.CENTER);
        rightContent.add(banner);
        rightContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // TODO [Backend]: ProductService.getByCategory(categoryName) → hiển thị thật
        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBackground(new Color(244, 244, 244));
        for (int i = 1; i <= 6; i++) {
            grid.add(createProductCard(mockName + " Phiên bản " + i, mockPrice));
        }
        rightContent.add(grid);

        JScrollPane scrollPane = new JScrollPane(rightContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        return listPanel;
    }

    // =========================================================================
    // MÀN HÌNH: CHI TIẾT SẢN PHẨM
    // =========================================================================
    private JPanel createProductDetailPanel(String productName, String productPrice) {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel breadcrumb = new JLabel("<html><a href=''>Trang chủ</a> &nbsp;&gt;&nbsp; Laptop gaming &nbsp;&gt;&nbsp; <b>" + productName + "</b></html>");
        breadcrumb.setFont(new Font("Arial", Font.PLAIN, 14));
        breadcrumb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breadcrumb.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        breadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(breadcrumb);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel topSection = new JPanel(new GridBagLayout());
        topSection.setBackground(Color.WHITE);
        topSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1.0;

        // Ảnh sản phẩm
        JPanel imageSection = new JPanel();
        imageSection.setLayout(new BoxLayout(imageSection, BoxLayout.Y_AXIS));
        imageSection.setBackground(Color.WHITE);
        JPanel mainImageMock = new JPanel(new BorderLayout());
        mainImageMock.setBackground(new Color(230, 230, 230));
        mainImageMock.setPreferredSize(new Dimension(450, 350));
        JLabel imgText = new JLabel("HÌNH ẢNH SẢN PHẨM", SwingConstants.CENTER);
        imgText.setForeground(Color.GRAY);
        imgText.setFont(new Font("Arial", Font.BOLD, 16));
        mainImageMock.add(imgText, BorderLayout.CENTER);
        imageSection.add(mainImageMock);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 30);
        topSection.add(imageSection, gbc);

        // Thông tin sản phẩm
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel("<html><div style='width:400px; font-size:18px; font-weight:bold;'>" + productName + "</div></html>");
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLbl = new JLabel("<html><span style='color:#E31C25; font-size:26px; font-weight:bold;'>"
            + productPrice + "</span> &nbsp;"
            + "<span style='text-decoration:line-through; color:#999; font-size:16px;'>33.090.000đ</span> &nbsp;"
            + "<span style='color:red; border:1px solid red; font-size:12px; padding:2px;'>-4%</span></html>");

        JButton buyBtn = new JButton("<html><center><b style='font-size:18px;'>MUA NGAY</b><br>"
            + "<span style='font-size:11px; font-weight:normal;'>Giao tận nơi/Nhận tại cửa hàng</span></center></html>");
        buyBtn.setBackground(new Color(227, 28, 37));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.setMaximumSize(new Dimension(400, 60));
        buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buyBtn.setBorder(new RoundedBorder(8, new Color(227, 28, 37)));
        // TODO [Backend]: CartService.addItem(productId, qty) → navigate to CART
        buyBtn.addActionListener(e -> addToCartAndShow(productName, productPrice));

        JPanel policyPanel = new JPanel();
        policyPanel.setLayout(new BoxLayout(policyPanel, BoxLayout.Y_AXIS));
        policyPanel.setBackground(Color.WHITE);
        for (String p : new String[]{
            "√ Bảo hành chính hãng 24 tháng.",
            "√ Hỗ trợ đổi mới trong 7 ngày.",
            "√ Miễn phí giao hàng toàn quốc."
        }) {
            JLabel pLbl = new JLabel(p);
            pLbl.setFont(new Font("Arial", Font.PLAIN, 15));
            policyPanel.add(pLbl);
            policyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        infoSection.add(titleLbl);
        infoSection.add(Box.createRigidArea(new Dimension(0, 15)));
        infoSection.add(priceLbl);
        infoSection.add(Box.createRigidArea(new Dimension(0, 20)));
        infoSection.add(buyBtn);
        infoSection.add(Box.createRigidArea(new Dimension(0, 25)));
        infoSection.add(policyPanel);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 0);
        topSection.add(infoSection, gbc);
        contentPanel.add(topSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Thông số kỹ thuật + Sản phẩm tương tự
        JPanel bottomSection = new JPanel(new GridBagLayout());
        bottomSection.setBackground(Color.WHITE);
        bottomSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbcBtm = new GridBagConstraints();
        gbcBtm.fill = GridBagConstraints.BOTH;
        gbcBtm.anchor = GridBagConstraints.NORTHWEST;
        gbcBtm.weighty = 1.0;

        JPanel specsWrapper = new JPanel(new BorderLayout(0, 10));
        specsWrapper.setBackground(Color.WHITE);
        JLabel specsTitle = new JLabel("Thông tin sản phẩm");
        specsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        specsWrapper.add(specsTitle, BorderLayout.NORTH);

        // TODO [Backend]: ProductService.getSpecs(productId)
        JPanel specsTable = new JPanel(new GridLayout(0, 2, 0, 0));
        specsTable.setBorder(new LineBorder(new Color(220, 220, 220)));
        String[][] specsData = {
            {"CPU",               "AMD Ryzen™ 7 8845HS"},
            {"Card đồ họa",       "NVIDIA® GeForce RTX™ 3050"},
            {"RAM",               "32GB (2x16GB) DDR5 5600MHz"},
            {"SSD",               "512GB PCIe NVMe SED SSD"},
            {"Kích thước màn hình", "16 inch"}
        };
        for (String[] rowData : specsData) {
            JPanel cell1 = new JPanel(new BorderLayout());
            cell1.setBackground(Color.WHITE);
            cell1.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 1, new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));
            cell1.add(new JLabel("<html><b>" + rowData[0] + "</b></html>"));
            JPanel cell2 = new JPanel(new BorderLayout());
            cell2.setBackground(Color.WHITE);
            cell2.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));
            cell2.add(new JLabel("<html><div style='width:180px;'>" + rowData[1] + "</div></html>"));
            specsTable.add(cell1);
            specsTable.add(cell2);
        }
        specsWrapper.add(specsTable, BorderLayout.CENTER);
        gbcBtm.gridx = 0; gbcBtm.gridy = 0; gbcBtm.weightx = 0.45;
        gbcBtm.insets = new Insets(0, 0, 0, 30);
        bottomSection.add(specsWrapper, gbcBtm);

        // TODO [Backend]: ProductService.getSimilar(productId)
        JPanel similarWrapper = new JPanel(new BorderLayout(0, 10));
        similarWrapper.setBackground(new Color(244, 244, 244));
        similarWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel similarTitle = new JLabel("Sản phẩm tương tự");
        similarTitle.setFont(new Font("Arial", Font.BOLD, 16));
        similarWrapper.add(similarTitle, BorderLayout.NORTH);
        JPanel similarGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        similarGrid.setBackground(new Color(244, 244, 244));
        similarGrid.add(createProductCard("Laptop gaming Acer Nitro...", "33.290.000đ"));
        similarGrid.add(createProductCard("Laptop gaming ASUS ROG...",  "48.490.000đ"));
        similarGrid.add(createProductCard("Laptop gaming ASUS ROG...",  "50.990.000đ"));
        similarWrapper.add(similarGrid, BorderLayout.CENTER);
        gbcBtm.gridx = 1; gbcBtm.gridy = 0; gbcBtm.weightx = 0.55;
        gbcBtm.insets = new Insets(0, 0, 0, 0);
        bottomSection.add(similarWrapper, gbcBtm);

        contentPanel.add(bottomSection);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }

    // =========================================================================
    // UI HELPERS — PRODUCT CARD & SECTION
    // =========================================================================
    private JPanel createProductSection(String titleText, String mockName, String mockPrice) {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(new Color(244, 244, 244));
        section.setMaximumSize(new Dimension(2000, 350));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        section.add(title, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(1, 4, 15, 0));
        grid.setBackground(new Color(244, 244, 244));
        for (int i = 1; i <= 4; i++) {
            grid.add(createProductCard(mockName + " V" + i, mockPrice));
        }
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel createProductCard(String name, String price) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220)));
        card.setPreferredSize(new Dimension(200, 280));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showProductDetail(name, price); }
        });

        JPanel imageMock = new JPanel();
        imageMock.setBackground(new Color(230, 230, 230));
        imageMock.setPreferredSize(new Dimension(200, 150));
        imageMock.setMaximumSize(new Dimension(300, 150));

        JTextArea nameLabel = new JTextArea(name);
        nameLabel.setWrapStyleWord(true);
        nameLabel.setLineWrap(true);
        nameLabel.setEditable(false);
        nameLabel.setFocusable(false);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        nameLabel.setBackground(Color.WHITE);
        nameLabel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(227, 28, 37));
        priceLabel.setBorder(new EmptyBorder(0, 10, 15, 10));

        card.add(imageMock);
        card.add(nameLabel);
        card.add(Box.createVerticalGlue());
        card.add(priceLabel);
        return card;
    }

    // =========================================================================
    // MÀN HÌNH: ĐĂNG NHẬP
    // =========================================================================
    private JPanel createLoginPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(244, 244, 244));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        box.setPreferredSize(new Dimension(450, 460));

        JPanel tabPanel = createTabPanel(true);
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JTextField emailField = createStyledTextField("E-mail");
        JPasswordField passField = createStyledPasswordField("Mật khẩu");

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        forgotPanel.setBackground(Color.WHITE);
        forgotPanel.setPreferredSize(new Dimension(370, 25));
        forgotPanel.setMaximumSize(new Dimension(370, 25));
        forgotPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel forgotLabel = new JLabel("<html><u>Quên mật khẩu</u></html>");
        forgotLabel.setForeground(Color.DARK_GRAY);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "FORGOT_PASS"); }
        });
        forgotPanel.add(forgotLabel);

        JButton loginBtn = createStyledButton("Đăng nhập", new Color(227, 28, 37));
        // TODO [Backend]: AuthService.login(email, password) → navigate đến ACCOUNT_INFO hoặc hiển thị lỗi
        loginBtn.addActionListener(e -> {
            refreshPage("ACCOUNT_INFO", createAccountInfoPanel());
            cardLayout.show(mainContentPanel, "ACCOUNT_INFO");
        });

        JLabel orLabel = new JLabel("hoặc đăng nhập bằng");
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setForeground(Color.GRAY);

        JPanel socialPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        socialPanel.setBackground(Color.WHITE);
        socialPanel.setPreferredSize(new Dimension(370, 42));
        socialPanel.setMaximumSize(new Dimension(370, 42));
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // TODO [Backend]: AuthService.loginWithGoogle() / loginWithFacebook()
        socialPanel.add(createStyledButton("Google",   new Color(219, 68, 55)));
        socialPanel.add(createStyledButton("Facebook", new Color(66, 103, 178)));

        form.add(emailField);  form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(passField);   form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(forgotPanel); form.add(Box.createRigidArea(new Dimension(0, 20)));
        form.add(loginBtn);    form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(orLabel);     form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(socialPanel);

        box.add(tabPanel); box.add(form);
        centerWrapper.add(box);
        return centerWrapper;
    }

    // =========================================================================
    // MÀN HÌNH: ĐĂNG KÝ
    // =========================================================================
    private JPanel createRegisterPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(244, 244, 244));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        box.setPreferredSize(new Dimension(450, 560));

        JPanel tabPanel = createTabPanel(false);
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JTextField emailField = createStyledTextField("E-mail");
        JTextField hoField    = createStyledTextField("Họ");
        JTextField tenField   = createStyledTextField("Tên");
        JPasswordField passField = createStyledPasswordField("Mật khẩu");

        JButton registerBtn = createStyledButton("Tạo tài khoản", new Color(227, 28, 37));
        // TODO [Backend]: AuthService.register(email, ho, ten, password) → navigate đến LOGIN hoặc hiển thị lỗi
        registerBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(GearVNApp.this,
                "Đăng ký thành công! Vui lòng đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainContentPanel, "LOGIN");
        });

        JLabel orLabel = new JLabel("hoặc đăng ký bằng");
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setForeground(Color.GRAY);

        JPanel socialPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        socialPanel.setBackground(Color.WHITE);
        socialPanel.setPreferredSize(new Dimension(370, 42));
        socialPanel.setMaximumSize(new Dimension(370, 42));
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        socialPanel.add(createStyledButton("Google",   new Color(219, 68, 55)));
        socialPanel.add(createStyledButton("Facebook", new Color(66, 103, 178)));

        form.add(emailField);   form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(hoField);      form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(tenField);     form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(passField);    form.add(Box.createRigidArea(new Dimension(0, 25)));
        form.add(registerBtn);  form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(orLabel);      form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(socialPanel);

        box.add(tabPanel); box.add(form);
        centerWrapper.add(box);
        return centerWrapper;
    }

    // =========================================================================
    // MÀN HÌNH: QUÊN MẬT KHẨU
    // =========================================================================
    private JPanel createForgotPasswordPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(244, 244, 244));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(227, 28, 37, 120), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        box.setPreferredSize(new Dimension(450, 260));

        JLabel titleLabel = new JLabel("Quên mật khẩu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(227, 28, 37));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputField = createStyledTextField("E-mail / SĐT");
        JButton submitBtn = createStyledButton("Gửi mã xác minh", new Color(227, 28, 37));
        Dimension btnSize = new Dimension(200, 42);
        submitBtn.setPreferredSize(btnSize); submitBtn.setMaximumSize(btnSize);
        // TODO [Backend]: AuthService.sendResetCode(email)

        JLabel backLabel = new JLabel("<html><u>Quay lại Đăng nhập</u></html>");
        backLabel.setForeground(Color.GRAY);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "LOGIN"); }
        });

        box.add(titleLabel); box.add(Box.createRigidArea(new Dimension(0, 25)));
        box.add(inputField); box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(submitBtn);  box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(backLabel);

        centerWrapper.add(box);
        return centerWrapper;
    }

    // =========================================================================
    // UI HELPERS — SHARED COMPONENTS
    // =========================================================================

    private JPanel createTabPanel(boolean isLoginActive) {
        JPanel tabPanel = new JPanel(new GridLayout(1, 2));
        tabPanel.setMaximumSize(new Dimension(450, 50));
        tabPanel.setPreferredSize(new Dimension(450, 50));
        tabPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tabLogin = new JLabel("Đăng nhập", SwingConstants.CENTER);
        tabLogin.setFont(new Font("Arial", Font.BOLD, 16));
        tabLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabLogin.setOpaque(true);
        tabLogin.setBackground(isLoginActive ? Color.WHITE : new Color(240, 240, 240));
        tabLogin.setBorder(isLoginActive
            ? new MatteBorder(3, 0, 0, 0, new Color(227, 28, 37))
            : new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tabLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "LOGIN"); }
        });

        JLabel tabReg = new JLabel("Đăng ký", SwingConstants.CENTER);
        tabReg.setFont(new Font("Arial", Font.BOLD, 16));
        tabReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabReg.setOpaque(true);
        tabReg.setBackground(!isLoginActive ? Color.WHITE : new Color(240, 240, 240));
        tabReg.setBorder(!isLoginActive
            ? new MatteBorder(3, 0, 0, 0, new Color(227, 28, 37))
            : new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tabReg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "REGISTER"); }
        });

        tabPanel.add(tabLogin);
        tabPanel.add(tabReg);
        return tabPanel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(370, 40));
        field.setMaximumSize(new Dimension(370, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) { field.setText(""); field.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) { field.setText(placeholder); field.setForeground(Color.GRAY); }
            }
        });
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(placeholder);
        field.setPreferredSize(new Dimension(370, 40));
        field.setMaximumSize(new Dimension(370, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText(""); field.setForeground(Color.BLACK); field.setEchoChar('●');
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder); field.setForeground(Color.GRAY); field.setEchoChar((char) 0);
                }
            }
        });
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(370, 42));
        btn.setMaximumSize(new Dimension(370, 42));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(5, bgColor));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // =========================================================================
    // INNER CLASS — Rounded Border
    // =========================================================================
    class RoundedBorder implements Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() { return true; }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // =========================================================================
    // ENTRY POINT
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GearVNApp().setVisible(true));
    }
}
