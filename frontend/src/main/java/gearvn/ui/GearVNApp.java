package gearvn.ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Properties;

public class GearVNApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Danh sách sản phẩm trong giỏ hàng: mỗi phần tử là {tên, giá, số lượng}
    private List<String[]> cartItems = new ArrayList<>();
    private JButton cartBtn; // tham chiếu để cập nhật badge số lượng

    public GearVNApp() {
        setTitle("GearVN - App Mua Sắm (All in One)");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER CHUNG ---
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

        // ── THANH TÌM KIẾM TRUNG TÂM ──────────────────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(0, 0));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(420, 38));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setText("Tìm kiếm sản phẩm...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 8)
        ));
        searchField.setBackground(new Color(255, 255, 255, 230));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm sản phẩm...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm sản phẩm...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchBtn = new JButton("🔍");
        searchBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchBtn.setBackground(new Color(200, 20, 30));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Runnable doSearch = () -> {
            String query = searchField.getText().trim();
            if (query.isEmpty() || query.equals("Tìm kiếm sản phẩm...")) return;
            for (Component c : mainContentPanel.getComponents()) {
                if ("SEARCH_RESULT".equals(c.getName())) { mainContentPanel.remove(c); break; }
            }
            JPanel srPanel = createSearchResultPanel(query);
            srPanel.setName("SEARCH_RESULT");
            mainContentPanel.add(srPanel, "SEARCH_RESULT");
            cardLayout.show(mainContentPanel, "SEARCH_RESULT");
        };
        searchBtn.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        // ── NÚT BÊN PHẢI (Đăng nhập + Tài khoản + Giỏ hàng) ─────────────────
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightHeaderPanel.setOpaque(false);

        JButton accountBtn = new JButton("Đăng nhập");
        accountBtn.setBackground(Color.WHITE);
        accountBtn.setForeground(new Color(227, 28, 37));
        accountBtn.setFocusPainted(false);
        accountBtn.setFont(new Font("Arial", Font.BOLD, 13));
        accountBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        accountBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "LOGIN"));

        JButton customerBtn = new JButton("👤 Tài khoản");
        customerBtn.setBackground(Color.WHITE);
        customerBtn.setForeground(new Color(227, 28, 37));
        customerBtn.setFocusPainted(false);
        customerBtn.setFont(new Font("Arial", Font.BOLD, 13));
        customerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        customerBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "CUSTOMER"));

        cartBtn = new JButton("🛒 Giỏ hàng (0)");
        cartBtn.setBackground(Color.WHITE);
        cartBtn.setForeground(new Color(227, 28, 37));
        cartBtn.setFocusPainted(false);
        cartBtn.setFont(new Font("Arial", Font.BOLD, 13));
        cartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cartBtn.addActionListener(e -> {
            refreshCartPanel();
            cardLayout.show(mainContentPanel, "CART");
        });

        JButton adminBtn = new JButton("⚙ Admin");
        adminBtn.setBackground(new Color(40, 40, 40));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setFocusPainted(false);
        adminBtn.setFont(new Font("Arial", Font.BOLD, 13));
        adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "ADMIN"));

        rightHeaderPanel.add(adminBtn);
        rightHeaderPanel.add(accountBtn);
        rightHeaderPanel.add(customerBtn);
        rightHeaderPanel.add(cartBtn);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. MAIN CONTENT (CardLayout quản lý các trang) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(244, 244, 244));

        // Nạp các trang cơ bản
        mainContentPanel.add(createHomePanel(), "HOME"); 
        mainContentPanel.add(createLoginPanel(), "LOGIN");
        mainContentPanel.add(createRegisterPanel(), "REGISTER");
        mainContentPanel.add(createForgotPasswordPanel(), "FORGOT_PASS");
        mainContentPanel.add(createCustomerPanel(), "CUSTOMER");
        
        // Trang giỏ hàng ban đầu (trống, sẽ được refresh khi thêm sản phẩm)
        JPanel initialCart = buildCartPanel();
        initialCart.setName("CART");
        mainContentPanel.add(initialCart, "CART");

        // Nạp các trang Danh sách sản phẩm
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

        // Nạp module Admin
        mainContentPanel.add(createAdminPanel(), "ADMIN");

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Mặc định hiện Home đầu tiên
        cardLayout.show(mainContentPanel, "HOME"); 
    }

    // =========================================================================
    // HÀM XỬ LÝ: CHI TIẾT SẢN PHẨM & THÊM VÀO GIỎ HÀNG
    // =========================================================================
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

    private void addToCartAndShow(String productName, String productPrice) {
        // Gọi API backend (chạy bất đồng bộ)
        new Thread(() -> {
            String json = "{ \"productName\":\"" + productName + "\", \"price\":\"" + productPrice + "\" }";
            ApiClient.post("/api/cart/add", json);
        }).start();

        // Kiểm tra sản phẩm đã có trong giỏ chưa → tăng số lượng
        boolean found = false;
        for (String[] item : cartItems) {
            if (item[0].equals(productName)) {
                item[2] = String.valueOf(Integer.parseInt(item[2]) + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            cartItems.add(new String[]{productName, productPrice, "1"});
        }

        // Cập nhật badge số lượng trên nút giỏ hàng header
        updateCartBadge();

        // Render lại giỏ hàng và chuyển trang
        refreshCartPanel();
        cardLayout.show(mainContentPanel, "CART");
    }

    /** Cập nhật text nút Giỏ hàng trên header với tổng số lượng */
    private void updateCartBadge() {
        int total = cartItems.stream().mapToInt(it -> Integer.parseInt(it[2])).sum();
        if (cartBtn != null) {
            cartBtn.setText("🛒 Giỏ hàng (" + total + ")");
        }
    }

    /** Xóa và tạo lại panel CART từ danh sách cartItems hiện tại */
    private void refreshCartPanel() {
        for (Component c : mainContentPanel.getComponents()) {
            if ("CART".equals(c.getName())) {
                mainContentPanel.remove(c);
                break;
            }
        }
        JPanel cartPanel = buildCartPanel();
        cartPanel.setName("CART");
        mainContentPanel.add(cartPanel, "CART");
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // =========================================================================
    // MÀN HÌNH GIỎ HÀNG (hỗ trợ nhiều sản phẩm)
    // =========================================================================
    private JPanel buildCartPanel() {
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
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(244, 244, 244));
        backPanel.setMaximumSize(new Dimension(900, 40));
        backPanel.add(backLabel);
        contentPanel.add(backPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Nếu giỏ trống
        if (cartItems.isEmpty()) {
            JPanel emptyBox = new JPanel(new BorderLayout());
            emptyBox.setBackground(Color.WHITE);
            emptyBox.setMaximumSize(new Dimension(900, 200));
            emptyBox.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
            JLabel emptyLbl = new JLabel("🛒  Giỏ hàng của bạn đang trống", SwingConstants.CENTER);
            emptyLbl.setFont(new Font("Arial", Font.PLAIN, 18));
            emptyLbl.setForeground(Color.GRAY);
            emptyBox.add(emptyLbl, BorderLayout.CENTER);
            contentPanel.add(emptyBox);
            JScrollPane sp = new JScrollPane(contentPanel);
            sp.setBorder(null);
            wrapperPanel.add(sp, BorderLayout.CENTER);
            return wrapperPanel;
        }

        // --- Hộp giỏ hàng ---
        JPanel cartBox = new JPanel();
        cartBox.setLayout(new BoxLayout(cartBox, BoxLayout.Y_AXIS));
        cartBox.setBackground(Color.WHITE);
        cartBox.setMaximumSize(new Dimension(900, Integer.MAX_VALUE));
        cartBox.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

        // Tính tổng tiền
        long[] totalRef = {computeTotal()};

        // Header tổng tiền
        JPanel totalHeader = new JPanel(new BorderLayout());
        totalHeader.setBackground(new Color(255, 240, 242));
        totalHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel totalText = new JLabel("Tổng tiền (" + cartItems.stream().mapToInt(it -> Integer.parseInt(it[2])).sum() + " sản phẩm):");
        totalText.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel totalPriceLabel = new JLabel(formatPrice(totalRef[0]));
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalPriceLabel.setForeground(new Color(227, 28, 37));
        totalHeader.add(totalText, BorderLayout.WEST);
        totalHeader.add(totalPriceLabel, BorderLayout.EAST);
        cartBox.add(totalHeader);
        cartBox.add(new JSeparator());

        // Render từng sản phẩm trong giỏ
        for (int idx = 0; idx < cartItems.size(); idx++) {
            final int i = idx;
            String[] item = cartItems.get(i);
            final String iName  = item[0];
            final String iPrice = item[1];
            final int[]  iQty   = {Integer.parseInt(item[2])};

            JPanel itemRow = new JPanel(new GridBagLayout());
            itemRow.setBackground(Color.WHITE);
            itemRow.setBorder(new EmptyBorder(15, 20, 15, 20));
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 8, 0, 8);
            gbc.anchor = GridBagConstraints.CENTER;

            // Cột 1: Ảnh giả
            JPanel imgMock = new JPanel(new BorderLayout());
            imgMock.setPreferredSize(new Dimension(90, 75));
            imgMock.setBackground(new Color(230, 230, 230));
            imgMock.add(new JLabel("IMG", SwingConstants.CENTER), BorderLayout.CENTER);
            gbc.gridx = 0; gbc.weightx = 0;
            itemRow.add(imgMock, gbc);

            // Cột 2: Tên sản phẩm
            JLabel nameLabel = new JLabel("<html><div style='width:230px; font-weight:bold;'>" + iName + "</div></html>");
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridx = 1; gbc.weightx = 0.45;
            itemRow.add(nameLabel, gbc);

            // Cột 3: Giá từng sản phẩm
            JLabel priceLabel = new JLabel(iPrice);
            priceLabel.setFont(new Font("Arial", Font.BOLD, 15));
            priceLabel.setForeground(new Color(227, 28, 37));
            gbc.gridx = 2; gbc.weightx = 0.2;
            itemRow.add(priceLabel, gbc);

            // Cột 4: Điều chỉnh số lượng & Nút Xóa
            JPanel actionPanel = new JPanel(new BorderLayout(0, 6));
            actionPanel.setBackground(Color.WHITE);

            // Nút Xóa
            JButton deleteBtn = new JButton("✕ Xóa");
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            deleteBtn.setForeground(Color.GRAY);
            deleteBtn.setBackground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.addActionListener(ev -> {
                cartItems.remove(i);
                updateCartBadge();
                refreshCartPanel();
                cardLayout.show(mainContentPanel, "CART");
            });

            // Bộ điều chỉnh số lượng
            JLabel qtyLabel = new JLabel(String.valueOf(iQty[0]), SwingConstants.CENTER);
            qtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
            qtyLabel.setPreferredSize(new Dimension(36, 28));

            JButton minusBtn = new JButton("-");
            minusBtn.setFont(new Font("Arial", Font.BOLD, 14));
            minusBtn.setFocusPainted(false);
            minusBtn.setPreferredSize(new Dimension(32, 28));
            minusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            minusBtn.addActionListener(ev -> {
                if (iQty[0] > 1) {
                    iQty[0]--;
                    cartItems.get(i)[2] = String.valueOf(iQty[0]);
                    updateCartBadge();
                    refreshCartPanel();
                    cardLayout.show(mainContentPanel, "CART");
                }
            });

            JButton plusBtn = new JButton("+");
            plusBtn.setFont(new Font("Arial", Font.BOLD, 14));
            plusBtn.setFocusPainted(false);
            plusBtn.setPreferredSize(new Dimension(32, 28));
            plusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            plusBtn.addActionListener(ev -> {
                iQty[0]++;
                cartItems.get(i)[2] = String.valueOf(iQty[0]);
                updateCartBadge();
                refreshCartPanel();
                cardLayout.show(mainContentPanel, "CART");
            });

            JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
            qtyPanel.setBackground(Color.WHITE);
            qtyPanel.add(minusBtn);
            qtyPanel.add(qtyLabel);
            qtyPanel.add(plusBtn);

            actionPanel.add(deleteBtn, BorderLayout.NORTH);
            actionPanel.add(qtyPanel, BorderLayout.SOUTH);
            gbc.gridx = 3; gbc.weightx = 0.15;
            itemRow.add(actionPanel, gbc);

            cartBox.add(itemRow);

            // Đường phân cách giữa các sản phẩm
            if (i < cartItems.size() - 1) {
                JSeparator sep = new JSeparator();
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                cartBox.add(sep);
            }
        }

        // Nút Đặt hàng ngay
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        JButton checkoutBtn = new JButton("ĐẶT HÀNG NGAY  →  " + formatPrice(totalRef[0]));
        checkoutBtn.setBackground(new Color(227, 28, 37));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 17));
        checkoutBtn.setPreferredSize(new Dimension(420, 52));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setBorder(new RoundedBorder(8, new Color(227, 28, 37)));
        checkoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Đặt hàng thành công!\nTổng tiền: " + formatPrice(computeTotal()),
                "Xác nhận đơn hàng", JOptionPane.INFORMATION_MESSAGE);
            cartItems.clear();
            updateCartBadge();
            refreshCartPanel();
            cardLayout.show(mainContentPanel, "HOME");
        });
        footerPanel.add(checkoutBtn);
        cartBox.add(footerPanel);

        contentPanel.add(cartBox);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }

    /** Tính tổng tiền từ danh sách cartItems (bỏ ký tự không phải số) */
    private long computeTotal() {
        long total = 0;
        for (String[] item : cartItems) {
            try {
                long price = Long.parseLong(item[1].replaceAll("[^0-9]", ""));
                int qty = Integer.parseInt(item[2]);
                total += price * qty;
            } catch (NumberFormatException ignored) {}
        }
        return total;
    }

    /** Định dạng số thành chuỗi tiền tệ VND */
    private String formatPrice(long amount) {
        return String.format("%,d", amount).replace(',', '.') + "đ";
    }

    // Giữ lại createCartPanel cũ (không dùng) để tránh lỗi biên dịch nếu còn tham chiếu
    @SuppressWarnings("unused")
    private JPanel createCartPanel(String productName, String productPrice) {
        return buildCartPanel();
    }

    // =========================================================================
    // 1. MÀN HÌNH TRANG CHỦ
    // =========================================================================
    private JPanel createHomePanel() {
        // [Nội dung giữ nguyên như code ban đầu của bạn]
        JPanel homePanel = new JPanel(new BorderLayout(15, 0));
        homePanel.setBackground(new Color(244, 244, 244));
        homePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new LineBorder(new Color(220, 220, 220)));

        String[] categories = {"Laptop Gaming", "Chuột Gaming", "Bàn Phím Gaming", "Tai Nghe Gaming", "Màn Hình", "Linh Kiện PC"};
        String[] linkKeys = {"LAPTOP_LIST", "CHUOT_LIST", "BAN_PHIM_LIST", "TAI_NGHE_LIST", "HOME", "HOME"};

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
                public void mouseExited(MouseEvent e) { catLabel.setForeground(Color.BLACK); }
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
    // 2. MÀN HÌNH DANH SÁCH SẢN PHẨM
    // =========================================================================
    private JPanel createProductListPanel(String categoryName, String[] priceRanges, String[] brands, String mockName, String mockPrice) {
        // [Nội dung giữ nguyên như code ban đầu của bạn]
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
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel priceLabel = new JLabel("Khoảng giá");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 15));
        filterSidebar.add(priceLabel);
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
    // 3. MÀN HÌNH CHI TIẾT SẢN PHẨM
    // =========================================================================
    private JPanel createProductDetailPanel(String productName, String productPrice) {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel breadcrumb = new JLabel("<html><a href=''>Trang chủ</a> &nbsp; > &nbsp; Laptop gaming &nbsp; > &nbsp; <b>" + productName + "</b></html>");
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

        JPanel imageSection = new JPanel();
        imageSection.setLayout(new BoxLayout(imageSection, BoxLayout.Y_AXIS));
        imageSection.setBackground(Color.WHITE);
        
        // ===== ẢNH CHÍNH =====
        JPanel mainImagePanel = new JPanel(new BorderLayout());
        mainImagePanel.setBackground(new Color(230, 230, 230));
        mainImagePanel.setPreferredSize(new Dimension(450, 350));
        mainImagePanel.setMaximumSize(new Dimension(450, 350));

        JLabel mainImgLabel = new JLabel("Đang tải ảnh...", SwingConstants.CENTER);
        mainImgLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        mainImgLabel.setForeground(Color.GRAY);
        mainImagePanel.add(mainImgLabel, BorderLayout.CENTER);

        // Nút 📷 để đổi ảnh ngay trong trang chi tiết
        JButton camBtnDetail = new JButton("📷 Đổi ảnh");
        camBtnDetail.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        camBtnDetail.setFocusPainted(false);
        camBtnDetail.setBackground(Color.WHITE);
        camBtnDetail.setBorder(new LineBorder(new Color(200,200,200)));
        camBtnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        camBtnDetail.addActionListener(ev -> showSetImageDialog(productName, mainImgLabel));
        JPanel camTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        camTopRight.setOpaque(false);
        camTopRight.add(camBtnDetail);
        mainImagePanel.add(camTopRight, BorderLayout.NORTH);

        // Load ảnh lên ảnh chính
        String detailImgUrl = getImageUrl(productName);
        if (!detailImgUrl.isEmpty()) {
            loadImageAsync450(mainImgLabel, detailImgUrl);
        }

        imageSection.add(mainImagePanel);
        imageSection.add(Box.createRigidArea(new Dimension(0, 10)));

        // ===== THUMBNAILS (4 ảnh nhỏ cùng link) =====
        JPanel thumbsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        thumbsPanel.setBackground(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            JPanel thumbWrapper = new JPanel(new BorderLayout());
            thumbWrapper.setBackground(new Color(230, 230, 230));
            thumbWrapper.setPreferredSize(new Dimension(80, 80));
            thumbWrapper.setBorder(new LineBorder(Color.LIGHT_GRAY));

            JLabel thumbImg = new JLabel("", SwingConstants.CENTER);
            thumbWrapper.add(thumbImg, BorderLayout.CENTER);

            if (!detailImgUrl.isEmpty()) {
                loadImageAsyncThumb(thumbImg, detailImgUrl);
            }
            thumbsPanel.add(thumbWrapper);
        }
        imageSection.add(thumbsPanel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 30);
        topSection.add(imageSection, gbc);

        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel("<html><div style='width: 400px; line-height: 1.2;'>" + productName + "</div></html>");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 22));
        
        JLabel priceLbl = new JLabel("<html><span style='color: #E31C25; font-size: 26px; font-weight: bold;'>" + productPrice 
                + "</span> &nbsp; <span style='text-decoration: line-through; color: #999999; font-size: 16px;'>33.090.000đ</span> &nbsp; <span style='color: red; border: 1px solid red; font-size: 12px; padding: 2px;'>-4%</span></html>");
        
        JButton buyBtn = new JButton("<html><center><b style='font-size: 18px;'>MUA NGAY</b><br><span style='font-size: 11px; font-weight: normal;'>Giao tận nơi/Nhận tại cửa hàng</span></center></html>");
        buyBtn.setBackground(new Color(227, 28, 37));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.setMaximumSize(new Dimension(400, 60));
        buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buyBtn.setBorder(new RoundedBorder(8, new Color(227, 28, 37)));

        // --- THÊM SỰ KIỆN CLICK ĐỂ CHUYỂN SANG GIỎ HÀNG ---
        buyBtn.addActionListener(e -> addToCartAndShow(productName, productPrice));

        JPanel policyPanel = new JPanel();
        policyPanel.setLayout(new BoxLayout(policyPanel, BoxLayout.Y_AXIS));
        policyPanel.setBackground(Color.WHITE);
        String[] policies = {
            "√ Bảo hành chính hãng 24 tháng.",
            "√ Hỗ trợ đổi mới trong 7 ngày.",
            "√ Miễn phí giao hàng toàn quốc."
        };
        for (String p : policies) {
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

        JPanel specsTable = new JPanel(new GridLayout(0, 2, 0, 0));
        specsTable.setBorder(new LineBorder(new Color(220, 220, 220)));
        String[][] specsData = {
            {"CPU", "AMD Ryzen™ 7 8845HS"},
            {"Card đồ họa", "NVIDIA® GeForce RTX™ 3050"},
            {"RAM", "32GB (2x16GB) DDR5 5600MHz"},
            {"SSD", "512GB PCIe NVMe SED SSD"},
            {"Kích thước màn hình", "16 inch"}
        };
        for (String[] rowData : specsData) {
            JPanel cell1 = new JPanel(new BorderLayout());
            cell1.setBackground(Color.WHITE);
            cell1.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 1, 1, new Color(220,220,220)), new EmptyBorder(10, 10, 10, 10)));
            cell1.add(new JLabel("<html><b>" + rowData[0] + "</b></html>"));
            
            JPanel cell2 = new JPanel(new BorderLayout());
            cell2.setBackground(Color.WHITE);
            cell2.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(220,220,220)), new EmptyBorder(10, 10, 10, 10)));
            cell2.add(new JLabel("<html><div style='width: 180px;'>" + rowData[1] + "</div></html>"));

            specsTable.add(cell1);
            specsTable.add(cell2);
        }
        specsWrapper.add(specsTable, BorderLayout.CENTER);
        
        gbcBtm.gridx = 0; gbcBtm.gridy = 0; gbcBtm.weightx = 0.45;
        gbcBtm.insets = new Insets(0, 0, 0, 30);
        bottomSection.add(specsWrapper, gbcBtm);

        JPanel similarWrapper = new JPanel(new BorderLayout(0, 10));
        similarWrapper.setBackground(new Color(244, 244, 244));
        similarWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel similarTitle = new JLabel("Sản phẩm tương tự");
        similarTitle.setFont(new Font("Arial", Font.BOLD, 16));
        similarWrapper.add(similarTitle, BorderLayout.NORTH);

        JPanel similarGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        similarGrid.setBackground(new Color(244, 244, 244));
        similarGrid.add(createProductCard("Laptop gaming Acer Nitro...", "33.290.000đ"));
        similarGrid.add(createProductCard("Laptop gaming ASUS ROG...", "48.490.000đ"));
        similarGrid.add(createProductCard("Laptop gaming ASUS ROG...", "50.990.000đ"));
        similarWrapper.add(similarGrid, BorderLayout.CENTER);

        gbcBtm.gridx = 1; gbcBtm.gridy = 0; gbcBtm.weightx = 0.55;
        gbcBtm.insets = new Insets(0, 0, 0, 0);
        bottomSection.add(similarWrapper, gbcBtm);

        contentPanel.add(bottomSection);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // =====================================================================
        // PHẦN ĐÁNH GIÁ SẢN PHẨM
        // =====================================================================
        JPanel reviewSection = buildReviewSection(productName);
        reviewSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(reviewSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }


    // =========================================================================
    // PHẦN ĐÁNH GIÁ SẢN PHẨM — REVIEW SECTION
    // =========================================================================
    private JPanel buildReviewSection(String productName) {
        Color RED   = new Color(227, 28, 37);
        Color GOLD  = new Color(255, 184, 0);
        Color LGREY = new Color(245, 245, 245);

        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 30, 30, 30)
        ));

        // ── Tiêu đề ─────────────────────────────────────────────────────────
        JLabel sectionTitle = new JLabel("Đánh giá & Nhận xét " + productName);
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 20));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Tổng quan điểm ─────────────────────────────────────────────────
        JPanel overviewRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        overviewRow.setBackground(Color.WHITE);
        overviewRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        overviewRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 54));
        scoreLabel.setForeground(new Color(40, 40, 40));

        JPanel scoreRight = new JPanel();
        scoreRight.setLayout(new BoxLayout(scoreRight, BoxLayout.Y_AXIS));
        scoreRight.setBackground(Color.WHITE);

        JPanel starsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starsRow.setBackground(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            JLabel star = new JLabel(i < 4 ? "★" : "☆");
            star.setFont(new Font("Arial", Font.PLAIN, 22));
            star.setForeground(i < 4 ? GOLD : new Color(200, 200, 200));
            starsRow.add(star);
        }

        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        countRow.setBackground(Color.WHITE);
        JLabel countLabel = new JLabel("(0 đánh giá)");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        countLabel.setForeground(Color.GRAY);
        JLabel helpIcon = createHelpIcon();
        countRow.add(countLabel);
        countRow.add(helpIcon);

        scoreRight.add(starsRow);
        scoreRight.add(Box.createRigidArea(new Dimension(0, 4)));
        scoreRight.add(countRow);

        overviewRow.add(scoreLabel);
        overviewRow.add(scoreRight);
        section.add(overviewRow);
        section.add(Box.createRigidArea(new Dimension(0, 24)));
        section.add(new JSeparator());
        section.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Form viết đánh giá ───────────────────────────────────────────────
        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setBackground(LGREY);
        formBox.setBorder(new EmptyBorder(20, 20, 20, 20));
        formBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        JLabel formTitle = new JLabel("Viết đánh giá của bạn");
        formTitle.setFont(new Font("Arial", Font.BOLD, 15));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBox.add(formTitle);
        formBox.add(Box.createRigidArea(new Dimension(0, 12)));

        // Chọn sao tương tác
        JPanel starPickRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        starPickRow.setBackground(LGREY);
        starPickRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel starPickLabel = new JLabel("Xếp hạng của bạn:");
        starPickLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        starPickRow.add(starPickLabel);
        starPickRow.add(Box.createHorizontalStrut(8));

        JLabel[] starBtns = new JLabel[5];
        int[] selectedStar = {0};

        for (int i = 0; i < 5; i++) {
            final int idx = i + 1;
            JLabel s = new JLabel("☆");
            s.setFont(new Font("Arial", Font.PLAIN, 26));
            s.setForeground(new Color(200, 200, 200));
            s.setCursor(new Cursor(Cursor.HAND_CURSOR));
            starBtns[i] = s;
            s.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selectedStar[0] = idx;
                    for (int j = 0; j < 5; j++) {
                        starBtns[j].setText(j < idx ? "★" : "☆");
                        starBtns[j].setForeground(j < idx ? GOLD : new Color(200, 200, 200));
                    }
                }
                public void mouseEntered(MouseEvent e) {
                    for (int j = 0; j < 5; j++) {
                        starBtns[j].setText(j < idx ? "★" : "☆");
                        starBtns[j].setForeground(j < idx ? GOLD : new Color(200, 200, 200));
                    }
                }
                public void mouseExited(MouseEvent e) {
                    int cur = selectedStar[0];
                    for (int j = 0; j < 5; j++) {
                        starBtns[j].setText(j < cur ? "★" : "☆");
                        starBtns[j].setForeground(j < cur ? GOLD : new Color(200, 200, 200));
                    }
                }
            });
            starPickRow.add(s);
        }
        formBox.add(starPickRow);
        formBox.add(Box.createRigidArea(new Dimension(0, 14)));

        // Tên người đánh giá
        JTextField reviewerName = new JTextField("Họ và tên");
        reviewerName.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewerName.setForeground(Color.GRAY);
        reviewerName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        reviewerName.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewerName.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        reviewerName.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (reviewerName.getText().equals("Họ và tên")) {
                    reviewerName.setText(""); reviewerName.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (reviewerName.getText().isEmpty()) {
                    reviewerName.setText("Họ và tên"); reviewerName.setForeground(Color.GRAY);
                }
            }
        });
        formBox.add(reviewerName);
        formBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Nội dung đánh giá
        JTextArea reviewText = new JTextArea(4, 0);
        reviewText.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewText.setForeground(Color.GRAY);
        reviewText.setText("Chia sẻ trải nghiệm của bạn về sản phẩm...");
        reviewText.setLineWrap(true);
        reviewText.setWrapStyleWord(true);
        reviewText.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        reviewText.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (reviewText.getText().startsWith("Chia sẻ")) {
                    reviewText.setText(""); reviewText.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (reviewText.getText().isEmpty()) {
                    reviewText.setText("Chia sẻ trải nghiệm của bạn về sản phẩm...");
                    reviewText.setForeground(Color.GRAY);
                }
            }
        });
        JScrollPane reviewScroll = new JScrollPane(reviewText);
        reviewScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        reviewScroll.setBorder(null);
        formBox.add(reviewScroll);
        formBox.add(Box.createRigidArea(new Dimension(0, 14)));

        // Danh sách đánh giá — khai báo ở đây để listener submit truy cập được
        JPanel reviewListPanel = new JPanel();
        reviewListPanel.setLayout(new BoxLayout(reviewListPanel, BoxLayout.Y_AXIS));
        reviewListPanel.setBackground(Color.WHITE);
        reviewListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel emptyReviewLbl = new JLabel("Chưa có đánh giá nào. Hãy là người đầu tiên đánh giá!");
        emptyReviewLbl.setFont(new Font("Arial", Font.ITALIC, 14));
        emptyReviewLbl.setForeground(Color.GRAY);
        emptyReviewLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewListPanel.add(emptyReviewLbl);

        // Nút gửi đánh giá
        JButton submitBtn = new JButton("GỬI ĐÁNH GIÁ");
        submitBtn.setBackground(RED);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setBorder(new RoundedBorder(6, RED));
        submitBtn.setPreferredSize(new Dimension(180, 42));
        submitBtn.setMaximumSize(new Dimension(180, 42));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> {
            String name   = reviewerName.getText().trim();
            String body   = reviewText.getText().trim();
            int    rating = selectedStar[0];
            if (name.isEmpty() || name.equals("Họ và tên")) {
                JOptionPane.showMessageDialog(GearVNApp.this,
                    "Vui lòng nhập họ và tên.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE); return;
            }
            if (rating == 0) {
                JOptionPane.showMessageDialog(GearVNApp.this,
                    "Vui lòng chọn số sao đánh giá.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE); return;
            }
            if (body.isEmpty() || body.startsWith("Chia sẻ")) {
                JOptionPane.showMessageDialog(GearVNApp.this,
                    "Vui lòng nhập nội dung đánh giá.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE); return;
            }
            String json = String.format(
                "{\"name\":\"%s\",\"rating\":%d,\"comment\":\"%s\"}",
                name, rating, body
            );
            System.out.println(json);
            reviewListPanel.remove(emptyReviewLbl);
            JPanel newCard = buildReviewCard(name, rating, body, GOLD);
            reviewListPanel.add(newCard);
            reviewListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            reviewListPanel.revalidate();
            reviewListPanel.repaint();
            // Reset form
            reviewerName.setText("Họ và tên"); reviewerName.setForeground(Color.GRAY);
            reviewText.setText("Chia sẻ trải nghiệm của bạn về sản phẩm..."); reviewText.setForeground(Color.GRAY);
            selectedStar[0] = 0;
            for (JLabel st : starBtns) { st.setText("☆"); st.setForeground(new Color(200, 200, 200)); }
            JOptionPane.showMessageDialog(GearVNApp.this,
                "Cảm ơn bạn đã đánh giá sản phẩm!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });
        formBox.add(submitBtn);
        section.add(formBox);
        section.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Danh sách nhận xét ───────────────────────────────────────────────
        JLabel listTitle = new JLabel("Nhận xét từ khách hàng");
        listTitle.setFont(new Font("Arial", Font.BOLD, 15));
        listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(listTitle);
        section.add(Box.createRigidArea(new Dimension(0, 12)));
        section.add(reviewListPanel);

        return section;
    }

    /** Tạo card hiển thị 1 đánh giá của khách hàng */
    private JPanel buildReviewCard(String author, int rating, String body, Color gold) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(14, 16, 14, 16)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));

        // Avatar vòng tròn chữ cái đầu + tên + sao
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setBackground(new Color(250, 250, 250));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatar = new JLabel(String.valueOf(author.charAt(0)).toUpperCase()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(227, 28, 37));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 16));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(false);

        JPanel nameStarCol = new JPanel();
        nameStarCol.setLayout(new BoxLayout(nameStarCol, BoxLayout.Y_AXIS));
        nameStarCol.setBackground(new Color(250, 250, 250));

        JLabel nameLbl = new JLabel(author);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        starsPanel.setBackground(new Color(250, 250, 250));
        for (int i = 0; i < 5; i++) {
            JLabel st = new JLabel(i < rating ? "★" : "☆");
            st.setFont(new Font("Arial", Font.PLAIN, 14));
            st.setForeground(i < rating ? gold : new Color(200, 200, 200));
            starsPanel.add(st);
        }
        nameStarCol.add(nameLbl);
        nameStarCol.add(starsPanel);
        headerRow.add(avatar);
        headerRow.add(nameStarCol);
        card.add(headerRow);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea bodyArea = new JTextArea(body);
        bodyArea.setWrapStyleWord(true); bodyArea.setLineWrap(true);
        bodyArea.setEditable(false);    bodyArea.setFocusable(false);
        bodyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        bodyArea.setBackground(new Color(250, 250, 250));
        bodyArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bodyArea);
        return card;
    }

    /** Icon dấu chấm hỏi nhỏ bên cạnh số đánh giá */
    private JLabel createHelpIcon() {
        JLabel icon = new JLabel("?") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(150, 150, 150));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Arial", Font.BOLD, 10));
        icon.setForeground(Color.WHITE);
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(16, 16));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        icon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null,
                    "Điểm đánh giá được tổng hợp từ tất cả\ncác nhận xét xác thực của khách hàng.",
                    "Thông tin đánh giá", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return icon;
    }

    // =========================================================================
    // CÁC HÀM UI HELPER (Tạo Thẻ SP, Tạo Form Login...)
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

    // ===== MAP ẢNH SẢN PHẨM =====
    private static final Map<String, String> PRODUCT_IMAGES = new HashMap<>();
    private static final Map<String, String> CUSTOM_IMAGES = new HashMap<>();
    private static final String IMAGE_PROPS_FILE = "product_images.properties";

    // ===== ĐỌC ẢNH CUSTOM TỪ FILE KHI KHỞI ĐỘNG =====
    static {
        Properties props = new Properties();
        File f = new File(IMAGE_PROPS_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
                for (String key : props.stringPropertyNames()) {
                    CUSTOM_IMAGES.put(key, props.getProperty(key));
                }
            } catch (IOException ignored) {}
        }
    }

    // ===== LƯU ẢNH CUSTOM XUỐNG FILE =====
    private void saveCustomImages() {
        Properties props = new Properties();
        props.putAll(CUSTOM_IMAGES);
        try (FileOutputStream fos = new FileOutputStream(IMAGE_PROPS_FILE)) {
            props.store(fos, "GearVN Product Images");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== MỞ DIALOG NHẬP LINK ẢNH =====
    private void showSetImageDialog(String productName, JLabel imgLabel) {
        JDialog dialog = new JDialog(this, "Cập nhật ảnh sản phẩm", true);
        dialog.setSize(520, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        titlePanel.setBackground(new Color(227, 28, 37));
        JLabel titleLbl = new JLabel("📷  Nhập link ảnh cho: " + productName);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 13));
        titlePanel.add(titleLbl);
        dialog.add(titlePanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        String current = CUSTOM_IMAGES.getOrDefault(productName, PRODUCT_IMAGES.getOrDefault(productName, ""));
        JTextField urlField = new JTextField(current.isEmpty() ? "https://..." : current);
        urlField.setFont(new Font("Arial", Font.PLAIN, 13));
        urlField.setPreferredSize(new Dimension(400, 35));
        urlField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (urlField.getText().equals("https://...")) urlField.setText("");
            }
        });
        inputPanel.add(new JLabel("Link PNG/JPG:"), BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.addActionListener(ev -> dialog.dispose());

        JButton saveBtn = new JButton("Lưu & Cập nhật");
        saveBtn.setBackground(new Color(227, 28, 37));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(ev -> {
            String url = urlField.getText().trim();
            if (url.isEmpty() || url.equals("https://...")) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập link ảnh!");
                return;
            }
            CUSTOM_IMAGES.put(productName, url);
            saveCustomImages();
            imgLabel.setIcon(null);
            imgLabel.setText("Đang tải...");
            // Tự phát hiện kích thước phù hợp theo component
            Dimension d = imgLabel.getPreferredSize();
            int w = (d != null && d.width > 200) ? 450 : 200;
            int h = (d != null && d.height > 150) ? 350 : 150;
            loadImageAsyncSized(imgLabel, url, w, h);
            dialog.dispose();
        });

        JButton clearBtn = new JButton("Xóa ảnh");
        clearBtn.addActionListener(ev -> {
            CUSTOM_IMAGES.remove(productName);
            saveCustomImages();
            imgLabel.setIcon(null);
            imgLabel.setText("Chưa có ảnh");
            dialog.dispose();
        });

        btnPanel.add(clearBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    static {
        // LAPTOP
        PRODUCT_IMAGES.put("Laptop ASUS ROG Strix G15",    "https://dlcdnwebimgs.asus.com/gain/6A2ACDBD-DD00-4F52-97CA-14AC0462BF88/w800");
        PRODUCT_IMAGES.put("Laptop ASUS ROG Zephyrus G14", "https://dlcdnwebimgs.asus.com/gain/3A3B74A8-D4E8-4BB1-A935-E5C793A78C0D/w800");
        PRODUCT_IMAGES.put("Laptop MSI Katana 15",          "https://asset.msi.com/resize/image/global/product/product_1_20220722183528_62daa120c67b5.png62405b38c58fe0f07fcef2367d8a9ba1/600.png");
        PRODUCT_IMAGES.put("Laptop Acer Nitro 5",           "https://images.acer.com/is/image/acer/acer-nitro5-wallpaper-feature?$Product-Cards-XL$");
        PRODUCT_IMAGES.put("Laptop Lenovo Legion 5",        "https://p1-ofp.static.pub/fes/cms/2022/07/13/qlqo2yru3vwhljymfywtmbecyb6zmh622804.png");
        PRODUCT_IMAGES.put("Laptop ASUS TUF Gaming F15",   "https://dlcdnwebimgs.asus.com/gain/5C3ABDC5-6B1A-4A27-B5CF-1DE9EC8BF2CC/w800");
        // CHUỘT
        PRODUCT_IMAGES.put("Chuột Razer DeathAdder Essential", "https://assets2.razerzone.com/images/pnx.assets/cc4b5e04-b30e-499c-857a-a6e03cb5148b/razer-deathadder-essential-gallery-4.jpg");
        PRODUCT_IMAGES.put("Chuột Razer Basilisk Ultimate",    "https://assets2.razerzone.com/images/pnx.assets/a80d7f3b-0834-4cc1-9df0-5e8d19a42898/razer-basilisk-ultimate-gallery-2.jpg");
        PRODUCT_IMAGES.put("Chuột Logitech G102",              "https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/mice/g102-lightsync/gallery/g102-lightsync-mouse-top-view-black.png");
        PRODUCT_IMAGES.put("Chuột Logitech G Pro X Superlight","https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/mice/pro-x-superlight-2/gallery/pro-x-superlight2-mouse-top-view-white.png");
        PRODUCT_IMAGES.put("Chuột Corsair M65 RGB Elite",     "https://www.corsair.com/medias/sys_master/images/images/h38/hb1/9057255039006.png");
        PRODUCT_IMAGES.put("Chuột SteelSeries Rival 3",       "https://steelseries.com/static/img/rival-3/rival-3-primary-black.png");
        // BÀN PHÍM
        PRODUCT_IMAGES.put("Bàn phím cơ AULA F75",       "https://salt.tikicdn.com/cache/750x750/ts/product/3d/e8/d2/54b3b1a4de8ad28ab0c8c5bf72bda03a.jpg");
        PRODUCT_IMAGES.put("Bàn phím AKKO 3098",          "https://salt.tikicdn.com/cache/750x750/ts/product/49/4d/da/93d3de9f87c0fb64a7a5e0b94c0f9fd6.jpg");
        PRODUCT_IMAGES.put("Bàn phím Corsair K70 RGB",   "https://www.corsair.com/medias/sys_master/images/images/hd6/h1c/8843001659422.png");
        PRODUCT_IMAGES.put("Bàn phím Logitech G Pro X",  "https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/keyboards/g-pro-x-keyboard/gallery/g-pro-x-keyboard-gallery-1-black.png");
        PRODUCT_IMAGES.put("Bàn phím Razer BlackWidow V3","https://assets2.razerzone.com/images/pnx.assets/b1f02f96-2d5a-4c97-8ea5-f8296ab487c5/razer-blackwidow-v3-gallery-1.jpg");
        PRODUCT_IMAGES.put("Bàn phím DareU EK87",        "https://salt.tikicdn.com/cache/750x750/ts/product/a5/5f/f8/94ea71e64b0e29a64a9e9440a2b34f4c.jpg");
        // TAI NGHE
        PRODUCT_IMAGES.put("Tai nghe Razer Barracuda X",    "https://assets2.razerzone.com/images/pnx.assets/4e2b2d04-ef09-481b-abf2-e4ace8b7cde0/razer-barracuda-x-gallery-1.jpg");
        PRODUCT_IMAGES.put("Tai nghe HyperX Cloud II",      "https://media.kingston.com/hyperx/product/hx-product-headset-cloud-ii-black-1-zm-lg.jpg");
        PRODUCT_IMAGES.put("Tai nghe Logitech G733",        "https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/gaming-headsets/g733/gallery/g733-headset-gallery-white-top.png");
        PRODUCT_IMAGES.put("Tai nghe Corsair HS80 RGB",     "https://www.corsair.com/medias/sys_master/images/images/h04/hb5/9057254940702.png");
        PRODUCT_IMAGES.put("Tai nghe SteelSeries Arctis 5", "https://steelseries.com/static/img/arctis-5/arctis-5-black.png");
        PRODUCT_IMAGES.put("Tai nghe ASUS TUF H3",          "https://dlcdnwebimgs.asus.com/gain/9c4c2a50-a3de-4b5a-9a02-f0cc44f01c10/w800");
        // MÀN HÌNH
        PRODUCT_IMAGES.put("Màn hình ASUS TUF 24 inch 144Hz", "https://dlcdnwebimgs.asus.com/gain/42F1B5B1-4D45-4B2A-A9C0-2AFD4FF3B55E/w800");
        PRODUCT_IMAGES.put("Màn hình MSI 27 inch 165Hz",      "https://asset.msi.com/resize/image/global/product/product_1_20220407134524_6250050ccc37d.png62405b38c58fe0f07fcef2367d8a9ba1/600.png");
        PRODUCT_IMAGES.put("Màn hình LG UltraGear 27GL850",   "https://www.lg.com/us/images/monitors/md07534540/gallery/desktop-01.jpg");
        PRODUCT_IMAGES.put("Màn hình Samsung Odyssey G5",     "https://image-us.samsung.com/SamsungUS/home/computing/monitors/gaming/06122020/LC27G55TQWNXZA_001_Front_Black.jpg");
        // PC PART
        PRODUCT_IMAGES.put("CPU Intel Core i5 13400F", "https://www.intel.com/content/dam/www/central-libraries/us/en/images/2022-11/processors-core-i5-13th-gen-badge-rwd.png");
        PRODUCT_IMAGES.put("CPU AMD Ryzen 5 5600X",    "https://www.amd.com/system/files/2020-10/616607-amd-ryzen-5-5600x-pib-left-facing-1260x709_0.png");
        PRODUCT_IMAGES.put("GPU RTX 4060",             "https://www.nvidia.com/content/nvidiaGDC/us/en_US/geforce/graphics-cards/40-series/rtx-4060/_jcr_content/root/responsivegrid/nv_container_392921705/container/nv_image.coreimg.100.1070.png/1687463798289/rtx4060-product-photo-001-v2.png");
        PRODUCT_IMAGES.put("GPU RTX 4070",             "https://www.nvidia.com/content/nvidiaGDC/us/en_US/geforce/graphics-cards/40-series/rtx-4070/_jcr_content/root/responsivegrid/nv_container_392921705/container/nv_image.coreimg.100.1070.png/1680633402585/rtx4070-product-photo-001.png");
        PRODUCT_IMAGES.put("RAM Corsair 16GB DDR4",    "https://www.corsair.com/medias/sys_master/images/images/hf5/hcc/8803049947166.png");
        PRODUCT_IMAGES.put("SSD Samsung 980 1TB",      "https://image-us.samsung.com/SamsungUS/home/computing/memory-storage/solid-state-drives/10012021/MZ-V8V1T0B_001_Front_Black.jpg");
    }

    // ===== LẤY URL ẢNH THEO TÊN SẢN PHẨM =====
    private String getImageUrl(String name) {
        // Ưu tiên ảnh custom do người dùng nhập
        String baseName = name.split(" V")[0].split(" Phiên bản")[0].trim();
        if (CUSTOM_IMAGES.containsKey(name)) return CUSTOM_IMAGES.get(name);
        if (CUSTOM_IMAGES.containsKey(baseName)) return CUSTOM_IMAGES.get(baseName);
        // Tìm chính xác trong map mặc định
        for (Map.Entry<String, String> entry : PRODUCT_IMAGES.entrySet()) {
            if (name.toLowerCase().contains(entry.getKey().toLowerCase()) ||
                entry.getKey().toLowerCase().contains(baseName.toLowerCase())) {
                return entry.getValue();
            }
        }
        // Fallback theo danh mục
        String lower = name.toLowerCase();
        int seed = Math.abs(name.hashCode() % 1000);
        if (lower.contains("laptop"))           return "https://picsum.photos/seed/laptop" + seed + "/200/150";
        if (lower.contains("chuột"))            return "https://picsum.photos/seed/mouse" + seed + "/200/150";
        if (lower.contains("bàn phím"))         return "https://picsum.photos/seed/keyboard" + seed + "/200/150";
        if (lower.contains("tai nghe"))         return "https://picsum.photos/seed/headset" + seed + "/200/150";
        if (lower.contains("màn hình"))         return "https://picsum.photos/seed/monitor" + seed + "/200/150";
        if (lower.contains("cpu") || lower.contains("gpu") || 
            lower.contains("ram") || lower.contains("ssd")) return "https://picsum.photos/seed/pcpart" + seed + "/200/150";
        return "https://picsum.photos/seed/" + seed + "/200/150";
    }

    // ===== LOAD ẢNH ASYNC (dùng chung, tham số width/height) =====
    private void loadImageAsyncSized(JLabel imgLabel, String imageUrl, int w, int h) {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    URL url = new URL(imageUrl);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(8000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedImage img = ImageIO.read(conn.getInputStream());
                    if (img != null) {
                        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                } catch (Exception ignored) {}
                return null;
            }
            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imgLabel.setIcon(icon);
                        imgLabel.setText("");
                        imgLabel.revalidate();
                        imgLabel.repaint();
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    private void loadImageAsync(JLabel imgLabel, String imageUrl) {
        loadImageAsyncSized(imgLabel, imageUrl, 200, 150);
    }

    private void loadImageAsync450(JLabel imgLabel, String imageUrl) {
        loadImageAsyncSized(imgLabel, imageUrl, 450, 350);
    }

    private void loadImageAsyncThumb(JLabel imgLabel, String imageUrl) {
        loadImageAsyncSized(imgLabel, imageUrl, 80, 80);
    }

    private JPanel createProductCard(String name, String price) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220)));
        card.setPreferredSize(new Dimension(200, 280));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showProductDetail(name, price);
            }
        });

        // Panel chứa ảnh + nút 📷
        JPanel imageWrapper = new JPanel(new BorderLayout());
        imageWrapper.setBackground(new Color(240, 240, 240));
        imageWrapper.setPreferredSize(new Dimension(200, 150));
        imageWrapper.setMaximumSize(new Dimension(300, 150));

        JLabel imgLabel = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        imgLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        imgLabel.setForeground(new Color(150, 150, 150));
        imageWrapper.add(imgLabel, BorderLayout.CENTER);

        // Nút 📷 góc trên phải
        JButton camBtn = new JButton("📷");
        camBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        camBtn.setPreferredSize(new Dimension(32, 24));
        camBtn.setMargin(new Insets(0, 0, 0, 0));
        camBtn.setFocusPainted(false);
        camBtn.setBackground(new Color(255, 255, 255, 200));
        camBtn.setBorder(new LineBorder(new Color(200, 200, 200)));
        camBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        camBtn.setToolTipText("Nhập link ảnh cho sản phẩm này");
        camBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                e.consume(); // không trigger click card
                showSetImageDialog(name, imgLabel);
            }
        });

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        topRight.setOpaque(false);
        topRight.add(camBtn);
        imageWrapper.add(topRight, BorderLayout.NORTH);

        // Load ảnh ban đầu
        String initUrl = getImageUrl(name);
        if (!initUrl.isEmpty()) {
            imgLabel.setText("Đang tải...");
            loadImageAsync(imgLabel, initUrl);
        }

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

        card.add(imageWrapper);
        card.add(nameLabel);
        card.add(Box.createVerticalGlue());
        card.add(priceLabel);

        return card;
    }

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
        JPasswordField passField = new JPasswordField("Password");
        passField.setPreferredSize(new Dimension(370, 40));
        passField.setMaximumSize(new Dimension(370, 40));
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passField.setForeground(Color.GRAY);
        passField.setEchoChar((char) 0); // Hiển thị placeholder dạng text
        passField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passField.getPassword()).equals("Password")) {
                    passField.setText("");
                    passField.setForeground(Color.BLACK);
                    passField.setEchoChar('●'); // Bật ẩn ký tự khi nhập thật
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passField.getPassword().length == 0) {
                    passField.setText("Password");
                    passField.setForeground(Color.GRAY);
                    passField.setEchoChar((char) 0); // Hiện lại placeholder
                }
            }
        });

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
        loginBtn.addActionListener(e -> {

            String email = emailField.getText().trim();
            String pass  = String.valueOf(passField.getPassword()).trim();

            if (email.isEmpty() || email.equals("E-mail") ||
                pass.isEmpty() || pass.equals("Password")) {
                JOptionPane.showMessageDialog(this, "Nhập email và password");
                return;
            }

            String json = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email.replace("\"", ""),
                pass.replace("\"", "")
            );

            new Thread(() -> {
                String res = ApiClient.post("/api/auth/login", json);

                SwingUtilities.invokeLater(() -> {
                    if (res != null && !res.isEmpty() && !res.equals("null")) {
                        JOptionPane.showMessageDialog(this, "Login OK");
                        cardLayout.show(mainContentPanel, "HOME");
                    } else {
                        JOptionPane.showMessageDialog(this, "Login FAIL (check backend)");
                    }
                });
            }).start();
        });
        JLabel orLabel = new JLabel("hoặc đăng nhập bằng");
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setForeground(Color.GRAY);
        
        JPanel socialPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        socialPanel.setBackground(Color.WHITE);
        socialPanel.setPreferredSize(new Dimension(370, 42));
        socialPanel.setMaximumSize(new Dimension(370, 42));
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        socialPanel.add(createStyledButton("Google", new Color(219, 68, 55)));
        socialPanel.add(createStyledButton("Facebook", new Color(66, 103, 178)));

        form.add(emailField); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(passField); form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(forgotPanel); form.add(Box.createRigidArea(new Dimension(0, 20)));
        form.add(loginBtn); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(orLabel); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(socialPanel);

        box.add(tabPanel); box.add(form); centerWrapper.add(box);
        return centerWrapper;
    }

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
        JTextField hoField = createStyledTextField("Họ");
        JTextField tenField = createStyledTextField("Tên");
        JPasswordField passField = new JPasswordField("Mật khẩu");
        passField.setPreferredSize(new Dimension(370, 40));
        passField.setMaximumSize(new Dimension(370, 40));
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passField.setForeground(Color.GRAY);
        passField.setEchoChar((char) 0);
        passField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passField.getPassword()).equals("Mật khẩu")) {
                    passField.setText("");
                    passField.setForeground(Color.BLACK);
                    passField.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passField.getPassword().length == 0) {
                    passField.setText("Mật khẩu");
                    passField.setForeground(Color.GRAY);
                    passField.setEchoChar((char) 0);
                }
            }
        });

        JButton registerBtn = createStyledButton("Tạo tài khoản", new Color(227, 28, 37));
        registerBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String ho    = hoField.getText().trim();
            String ten   = tenField.getText().trim();
            String pass  = String.valueOf(passField.getPassword()).trim();

            if (email.equals("E-mail") || email.isEmpty() ||
                ho.equals("Họ") || ho.isEmpty() ||
                ten.equals("Tên") || ten.isEmpty() ||
                pass.equals("Mật khẩu") || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
                return;
            }

            String json = String.format(
                "{\"email\":\"%s\",\"name\":\"%s %s\",\"password\":\"%s\"}",
                email.replace("\"", ""),
                ho.replace("\"", ""),
                ten.replace("\"", ""),
                pass.replace("\"", "")
            );

            new Thread(() -> {
                String res = ApiClient.post("/api/auth/register", json);
                SwingUtilities.invokeLater(() -> {
                    if (res != null && !res.isEmpty() && !res.equals("null")) {
                        JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mời đăng nhập.");
                        cardLayout.show(mainContentPanel, "LOGIN");
                    } else {
                        JOptionPane.showMessageDialog(this, "Đăng ký thất bại (email đã tồn tại?)");
                    }
                });
            }).start();
        });

        JLabel orLabel = new JLabel("hoặc đăng ký bằng");
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setForeground(Color.GRAY);
        
        JPanel socialPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        socialPanel.setBackground(Color.WHITE);
        socialPanel.setPreferredSize(new Dimension(370, 42));
        socialPanel.setMaximumSize(new Dimension(370, 42));
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        socialPanel.add(createStyledButton("Google", new Color(219, 68, 55)));
        socialPanel.add(createStyledButton("Facebook", new Color(66, 103, 178)));

        form.add(emailField); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(hoField);    form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(tenField);   form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(passField);  form.add(Box.createRigidArea(new Dimension(0, 25)));
        form.add(registerBtn);form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(orLabel);    form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(socialPanel);

        box.add(tabPanel); box.add(form); centerWrapper.add(box);
        return centerWrapper;
    }

    private JPanel createForgotPasswordPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(244, 244, 244));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(227, 28, 37, 120), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        box.setPreferredSize(new Dimension(450, 260));

        JLabel titleLabel = new JLabel("Quên mật khẩu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(227, 28, 37));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputField = createStyledTextField("E-mail / SĐT");
        JButton submitBtn = createStyledButton("Gửi mã xác minh", new Color(227, 28, 37));
        Dimension btnSize = new Dimension(200, 42);
        submitBtn.setPreferredSize(btnSize); submitBtn.setMaximumSize(btnSize);

        JLabel backLabel = new JLabel("<html><u>Quay lại Đăng nhập</u></html>");
        backLabel.setForeground(Color.GRAY);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "LOGIN"); }
        });

        box.add(titleLabel); box.add(Box.createRigidArea(new Dimension(0, 25)));
        box.add(inputField); box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(submitBtn); box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(backLabel);

        centerWrapper.add(box);
        return centerWrapper;
    }

    // --- Các hàm hỗ trợ do đoạn code bị thiếu phần cuối ---
    
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
        tabLogin.setBorder(isLoginActive ? new MatteBorder(3, 0, 0, 0, new Color(227, 28, 37)) : new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tabLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "LOGIN"); }
        });

        JLabel tabReg = new JLabel("Đăng ký", SwingConstants.CENTER);
        tabReg.setFont(new Font("Arial", Font.BOLD, 16));
        tabReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabReg.setOpaque(true);
        tabReg.setBackground(!isLoginActive ? Color.WHITE : new Color(240, 240, 240));
        tabReg.setBorder(!isLoginActive ? new MatteBorder(3, 0, 0, 0, new Color(227, 28, 37)) : new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
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

    // --- Lớp hỗ trợ vẽ viền bo tròn (Rounded Border) ---
    class RoundedBorder implements Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // =========================================================================
    // TRANG KẾT QUẢ TÌM KIẾM
    // =========================================================================
    private JPanel createSearchResultPanel(String query) {
        Color RED = new Color(227, 28, 37);

        // Dữ liệu mẫu – trong thực tế sẽ gọi API
        String[][] allProducts = {
            {"Laptop ASUS ROG Strix",          "31.790.000đ", "laptop"},
            {"Laptop gaming Acer Nitro 5",      "22.490.000đ", "laptop"},
            {"Laptop Lenovo Legion 5",           "28.990.000đ", "laptop"},
            {"Laptop MSI Katana 15",             "24.990.000đ", "laptop"},
            {"Laptop ASUS TUF Gaming F15",       "20.490.000đ", "laptop"},
            {"Chuột Razer DeathAdder Essential", "1.590.000đ",  "chuột"},
            {"Chuột Logitech G102",              "450.000đ",    "chuột"},
            {"Chuột Corsair M65 RGB Elite",      "1.890.000đ",  "chuột"},
            {"Bàn phím cơ AULA F75",             "650.000đ",    "bàn phím"},
            {"Bàn phím AKKO 3098",               "1.290.000đ",  "bàn phím"},
            {"Bàn phím Corsair K70 RGB",         "3.490.000đ",  "bàn phím"},
            {"Tai nghe Razer Barracuda X",       "2.890.000đ",  "tai nghe"},
            {"Tai nghe HyperX Cloud II",         "1.790.000đ",  "tai nghe"},
            {"Tai nghe Logitech G733",           "2.490.000đ",  "tai nghe"},
        };

        String lq = query.toLowerCase();
        java.util.List<String[]> results = new java.util.ArrayList<>();
        for (String[] p : allProducts) {
            if (p[0].toLowerCase().contains(lq) || p[2].toLowerCase().contains(lq)) {
                results.add(p);
            }
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(244, 244, 244));

        // ── Breadcrumb + tiêu đề ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel breadcrumb = new JLabel(
            "<html><span style='color:#888;cursor:pointer;'>Trang chủ</span> &nbsp;›&nbsp; "
            + "<b>Kết quả tìm kiếm cho: \"" + query + "\"</b></html>");
        breadcrumb.setFont(new Font("Arial", Font.PLAIN, 14));
        breadcrumb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breadcrumb.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });

        JLabel countLbl = new JLabel("Tìm thấy " + results.size() + " sản phẩm");
        countLbl.setFont(new Font("Arial", Font.ITALIC, 13));
        countLbl.setForeground(Color.GRAY);

        topBar.add(breadcrumb, BorderLayout.WEST);
        topBar.add(countLbl, BorderLayout.EAST);
        wrapper.add(topBar, BorderLayout.NORTH);

        // ── Khu vực kết quả ──────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(244, 244, 244));
        content.setBorder(new EmptyBorder(15, 20, 20, 20));

        if (results.isEmpty()) {
            JPanel noResult = new JPanel(new GridBagLayout());
            noResult.setBackground(new Color(244, 244, 244));
            noResult.setPreferredSize(new Dimension(800, 200));
            JLabel noLbl = new JLabel("<html><center><b style='font-size:16px;'>Không tìm thấy sản phẩm nào</b><br>"
                + "<span style='color:#888;'>Hãy thử từ khóa khác như: laptop, chuột, bàn phím, tai nghe...</span></center></html>");
            noLbl.setHorizontalAlignment(SwingConstants.CENTER);
            noResult.add(noLbl);
            content.add(noResult);
        } else {
            // Hiển thị theo grid 3 cột
            int cols = 3;
            int rows = (results.size() + cols - 1) / cols;
            for (int r = 0; r < rows; r++) {
                JPanel row = new JPanel(new GridLayout(1, cols, 15, 0));
                row.setBackground(new Color(244, 244, 244));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
                for (int c = 0; c < cols; c++) {
                    int idx = r * cols + c;
                    if (idx < results.size()) {
                        row.add(createProductCard(results.get(idx)[0], results.get(idx)[1]));
                    } else {
                        JPanel empty = new JPanel(); empty.setOpaque(false); row.add(empty);
                    }
                }
                content.add(row);
                content.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // =========================================================================
    // TRANG KHÁCH HÀNG — Thông tin & Theo dõi đơn hàng
    // =========================================================================
    private JPanel createCustomerPanel() {
        Color RED   = new Color(227, 28, 37);
        Color LGREY = new Color(244, 244, 244);
        Color WHITE = Color.WHITE;

        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setBackground(LGREY);

        // ── Breadcrumb ────────────────────────────────────────────────────────
        JPanel breadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        breadPanel.setBackground(WHITE);
        JLabel bread = new JLabel("<html><span style='color:#888;cursor:pointer;'>Trang chủ</span> &nbsp;›&nbsp; <b>Tài khoản của tôi</b></html>");
        bread.setFont(new Font("Arial", Font.PLAIN, 14));
        bread.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bread.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        breadPanel.add(bread);
        outerWrapper.add(breadPanel, BorderLayout.NORTH);

        // ── Body: Sidebar tabs + Content ──────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(15, 0));
        body.setBackground(LGREY);
        body.setBorder(new EmptyBorder(15, 20, 20, 20));

        // ── Sidebar ────────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(WHITE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(0, 0, 10, 0)
        ));

        // Avatar / tên ngắn
        JPanel avatarBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        avatarBox.setBackground(RED);
        avatarBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel avLbl = new JLabel("NK") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avLbl.setFont(new Font("Arial", Font.BOLD, 16));
        avLbl.setForeground(WHITE);
        avLbl.setPreferredSize(new Dimension(40, 40));
        avLbl.setHorizontalAlignment(SwingConstants.CENTER);
        avLbl.setOpaque(false);
        JLabel nameLbl = new JLabel("<html><b style='color:white; font-size:13px;'>Nguyễn Khách</b>"
            + "<br><span style='color:#ffcccc; font-size:11px;'>khach@gmail.com</span></html>");
        avatarBox.add(avLbl);
        avatarBox.add(nameLbl);
        sidebar.add(avatarBox);

        // Dùng CardLayout nội bộ cho nội dung tab
        CardLayout tabCard = new CardLayout();
        JPanel tabContent = new JPanel(tabCard);
        tabContent.setBackground(LGREY);

        String[] tabNames   = {"Thông tin tài khoản", "Đơn hàng của tôi",   "Địa chỉ giao hàng", "Đổi mật khẩu"};
        String[] tabKeys    = {"INFO",                  "ORDERS",              "ADDRESS",            "PASS"};
        String[] tabIcons   = {"👤",                    "📦",                  "📍",                 "🔒"};
        JLabel[] tabLabels  = new JLabel[tabNames.length];

        for (int i = 0; i < tabNames.length; i++) {
            final int idx = i;
            final String key = tabKeys[i];
            JLabel tab = new JLabel("<html>&nbsp;" + tabIcons[i] + "&nbsp;&nbsp;" + tabNames[i] + "</html>");
            tab.setFont(new Font("Arial", Font.PLAIN, 14));
            tab.setOpaque(true);
            tab.setBackground(WHITE);
            tab.setPreferredSize(new Dimension(220, 46));
            tab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tabLabels[i] = tab;
            tab.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    tabCard.show(tabContent, key);
                    for (JLabel tl : tabLabels) {
                        tl.setBackground(WHITE);
                        tl.setBorder(new MatteBorder(0, 0, 1, 0, new Color(235, 235, 235)));
                    }
                    tabLabels[idx].setBackground(new Color(255, 240, 240));
                    tabLabels[idx].setBorder(new MatteBorder(0, 3, 1, 0, RED));
                }
                public void mouseEntered(MouseEvent e) { if (!tab.getBackground().equals(new Color(255, 240, 240))) tab.setBackground(new Color(250, 250, 250)); }
                public void mouseExited(MouseEvent e)  { if (!tab.getBackground().equals(new Color(255, 240, 240))) tab.setBackground(WHITE); }
            });
            tab.setBorder(new MatteBorder(0, 0, 1, 0, new Color(235, 235, 235)));
            if (i == 0) {
                tab.setBackground(new Color(255, 240, 240));
                tab.setBorder(new MatteBorder(0, 3, 1, 0, RED));
            }
            sidebar.add(tab);
        }

        body.add(sidebar, BorderLayout.WEST);

        // ── TAB 1: THÔNG TIN TÀI KHOẢN ───────────────────────────────────────
        JPanel infoTab = new JPanel();
        infoTab.setLayout(new BoxLayout(infoTab, BoxLayout.Y_AXIS));
        infoTab.setBackground(LGREY);
        infoTab.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 30, 30, 30)
        ));

        JLabel infoTitle = new JLabel("Thông tin cá nhân");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 18));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(infoTitle);
        infoCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Avatar lớn
        JPanel bigAvatar = new JPanel(new BorderLayout());
        bigAvatar.setOpaque(false);
        bigAvatar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        bigAvatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel bigAv = new JLabel("NK") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bigAv.setFont(new Font("Arial", Font.BOLD, 26));
        bigAv.setForeground(WHITE);
        bigAv.setPreferredSize(new Dimension(72, 72));
        bigAv.setHorizontalAlignment(SwingConstants.CENTER);
        bigAv.setOpaque(false);
        JPanel avWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        avWrap.setOpaque(false);
        avWrap.add(bigAv);
        bigAvatar.add(avWrap, BorderLayout.WEST);
        infoCard.add(bigAvatar);
        infoCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Fields thông tin
        String[][] fields = {
            {"Họ và tên",    "Nguyễn Khách"},
            {"Email",        "khach@gmail.com"},
            {"Số điện thoại","0909 123 456"},
            {"Ngày sinh",    "01/01/1995"},
            {"Giới tính",    "Nam"},
        };
        for (String[] f : fields) {
            JPanel row = new JPanel(new BorderLayout(15, 0));
            row.setBackground(new Color(248, 248, 248));
            row.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
                new EmptyBorder(11, 14, 11, 14)
            ));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel kLbl = new JLabel(f[0]);
            kLbl.setFont(new Font("Arial", Font.BOLD, 13));
            kLbl.setForeground(new Color(100, 100, 100));
            kLbl.setPreferredSize(new Dimension(140, 24));
            JLabel vLbl = new JLabel(f[1]);
            vLbl.setFont(new Font("Arial", Font.PLAIN, 14));
            row.add(kLbl, BorderLayout.WEST);
            row.add(vLbl, BorderLayout.CENTER);
            infoCard.add(row);
        }
        infoCard.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton editBtn = new JButton("Chỉnh sửa thông tin");
        editBtn.setBackground(RED);
        editBtn.setForeground(WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setFont(new Font("Arial", Font.BOLD, 14));
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.setBorder(new RoundedBorder(6, RED));
        editBtn.setPreferredSize(new Dimension(200, 42));
        editBtn.addActionListener(ev ->
            JOptionPane.showMessageDialog(this, "Chức năng chỉnh sửa sẽ kết nối API backend.", "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        btnRow.add(editBtn);
        infoCard.add(btnRow);

        infoTab.add(infoCard);
        tabContent.add(infoTab, "INFO");

        // ── TAB 2: ĐƠN HÀNG / THEO DÕI ──────────────────────────────────────
        JPanel ordersTab = new JPanel();
        ordersTab.setLayout(new BoxLayout(ordersTab, BoxLayout.Y_AXIS));
        ordersTab.setBackground(LGREY);

        JPanel ordersCard = new JPanel();
        ordersCard.setLayout(new BoxLayout(ordersCard, BoxLayout.Y_AXIS));
        ordersCard.setBackground(WHITE);
        ordersCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 25, 25, 25)
        ));

        JLabel ordersTitle = new JLabel("Đơn hàng của tôi");
        ordersTitle.setFont(new Font("Arial", Font.BOLD, 18));
        ordersTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        ordersCard.add(ordersTitle);
        ordersCard.add(Box.createRigidArea(new Dimension(0, 18)));

        // Bộ lọc trạng thái đơn hàng
        String[] statusFilters = {"Tất cả", "Chờ xác nhận", "Đang giao", "Đã giao", "Đã hủy"};
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filterRow.setBackground(WHITE);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel[] filterBtns = new JLabel[statusFilters.length];
        for (int i = 0; i < statusFilters.length; i++) {
            final int fi = i;
            JLabel fb = new JLabel(statusFilters[i]);
            fb.setFont(new Font("Arial", Font.PLAIN, 13));
            fb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            fb.setOpaque(true);
            fb.setBorder(new EmptyBorder(5, 12, 5, 12));
            fb.setBackground(i == 0 ? RED : new Color(240, 240, 240));
            fb.setForeground(i == 0 ? WHITE : Color.DARK_GRAY);
            filterBtns[i] = fb;
            fb.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    for (JLabel f2 : filterBtns) { f2.setBackground(new Color(240, 240, 240)); f2.setForeground(Color.DARK_GRAY); }
                    filterBtns[fi].setBackground(RED); filterBtns[fi].setForeground(WHITE);
                }
            });
            filterRow.add(fb);
        }
        ordersCard.add(filterRow);
        ordersCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Dữ liệu đơn hàng mẫu
        Object[][] orders = {
            {"#GVN20240501", "Laptop ASUS ROG Strix G15",         "31.790.000đ", "Đang giao",     new int[]{1,1,1,0,0}},
            {"#GVN20240418", "Chuột Razer DeathAdder Essential", "1.590.000đ",  "Đã giao",       new int[]{1,1,1,1,1}},
            {"#GVN20240310", "Bàn phím cơ AULA F75",              "650.000đ",    "Đã giao",       new int[]{1,1,1,1,1}},
            {"#GVN20240205", "Tai nghe HyperX Cloud II",           "1.790.000đ",  "Đã hủy",        new int[]{1,0,0,0,0}},
        };

        for (Object[] ord : orders) {
            String ordId    = (String) ord[0];
            String ordName  = (String) ord[1];
            String ordPrice = (String) ord[2];
            String ordSts   = (String) ord[3];
            int[]  steps    = (int[])  ord[4];

            Color stsColor;
            switch (ordSts) {
                case "Đang giao":     stsColor = new Color(255, 140, 0);  break;
                case "Đã giao":       stsColor = new Color(39, 174, 96);  break;
                case "Đã hủy":        stsColor = new Color(180, 0, 0);    break;
                default:              stsColor = new Color(100, 100, 200);break;
            }

            JPanel orderCard = new JPanel();
            orderCard.setLayout(new BoxLayout(orderCard, BoxLayout.Y_AXIS));
            orderCard.setBackground(new Color(252, 252, 252));
            orderCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 18, 15, 18)
            ));
            orderCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            orderCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

            // Header dòng đơn hàng
            JPanel oHeader = new JPanel(new BorderLayout());
            oHeader.setOpaque(false);
            JLabel oIdLbl = new JLabel(ordId + " — " + ordName);
            oIdLbl.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel oPriceLbl = new JLabel(ordPrice);
            oPriceLbl.setFont(new Font("Arial", Font.BOLD, 15));
            oPriceLbl.setForeground(RED);
            JLabel oStsLbl = new JLabel("  " + ordSts + "  ");
            oStsLbl.setFont(new Font("Arial", Font.BOLD, 12));
            oStsLbl.setForeground(WHITE);
            oStsLbl.setOpaque(true);
            oStsLbl.setBackground(stsColor);
            oStsLbl.setBorder(new EmptyBorder(3, 8, 3, 8));
            JPanel oRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            oRight.setOpaque(false);
            oRight.add(oPriceLbl);
            oRight.add(oStsLbl);
            oHeader.add(oIdLbl, BorderLayout.WEST);
            oHeader.add(oRight, BorderLayout.EAST);
            orderCard.add(oHeader);
            orderCard.add(Box.createRigidArea(new Dimension(0, 12)));

            // Thanh tiến trình giao hàng
            if (!ordSts.equals("Đã hủy")) {
                String[] stepNames = {"Đặt hàng", "Xác nhận", "Đóng gói", "Đang giao", "Đã nhận"};
                JPanel trackBar = new JPanel(new GridLayout(1, stepNames.length));
                trackBar.setOpaque(false);
                trackBar.setAlignmentX(Component.LEFT_ALIGNMENT);
                trackBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
                for (int s = 0; s < stepNames.length; s++) {
                    boolean done = steps[s] == 1;
                    JPanel step = new JPanel();
                    step.setLayout(new BoxLayout(step, BoxLayout.Y_AXIS));
                    step.setOpaque(false);

                    JPanel dotRow = new JPanel();
                    dotRow.setOpaque(false);
                    dotRow.setLayout(new BoxLayout(dotRow, BoxLayout.X_AXIS));

                    // Đường nối trái
                    JPanel lineL = new JPanel() {
                        protected void paintComponent(Graphics g) {
                            g.setColor(done ? RED : new Color(210, 210, 210));
                            g.fillRect(0, getHeight()/2 - 1, getWidth(), 2);
                        }
                    };
                    lineL.setOpaque(false);
                    lineL.setPreferredSize(new Dimension(20, 20));

                    // Chấm tròn
                    JLabel dot = new JLabel("") {
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(done ? RED : new Color(210, 210, 210));
                            g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                            if (done) { g2.setColor(WHITE); g2.drawString("✓", 3, 14); }
                            g2.dispose();
                        }
                    };
                    dot.setPreferredSize(new Dimension(22, 22));
                    dot.setMaximumSize(new Dimension(22, 22));
                    dot.setFont(new Font("Arial", Font.BOLD, 10));
                    dot.setOpaque(false);

                    // Đường nối phải
                    JPanel lineR = new JPanel() {
                        protected void paintComponent(Graphics g) {
                            g.setColor(done ? RED : new Color(210, 210, 210));
                            g.fillRect(0, getHeight()/2 - 1, getWidth(), 2);
                        }
                    };
                    lineR.setOpaque(false);
                    lineR.setPreferredSize(new Dimension(20, 20));

                    if (s == 0) { JPanel sp = new JPanel(); sp.setOpaque(false); dotRow.add(sp); }
                    else dotRow.add(lineL);
                    dotRow.add(dot);
                    if (s == stepNames.length - 1) { JPanel sp = new JPanel(); sp.setOpaque(false); dotRow.add(sp); }
                    else dotRow.add(lineR);

                    JLabel stepLbl = new JLabel(stepNames[s], SwingConstants.CENTER);
                    stepLbl.setFont(new Font("Arial", done ? Font.BOLD : Font.PLAIN, 11));
                    stepLbl.setForeground(done ? RED : new Color(150, 150, 150));
                    stepLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

                    step.add(dotRow);
                    step.add(Box.createRigidArea(new Dimension(0, 4)));
                    step.add(stepLbl);
                    trackBar.add(step);
                }
                orderCard.add(trackBar);
            } else {
                JLabel cancelLbl = new JLabel("Đơn hàng đã bị hủy");
                cancelLbl.setFont(new Font("Arial", Font.ITALIC, 13));
                cancelLbl.setForeground(new Color(180, 0, 0));
                cancelLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                orderCard.add(cancelLbl);
            }

            ordersCard.add(orderCard);
            ordersCard.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        ordersTab.add(ordersCard);

        JScrollPane orderScroll = new JScrollPane(ordersTab);
        orderScroll.setBorder(null);
        orderScroll.getVerticalScrollBar().setUnitIncrement(16);
        orderScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel orderScrollWrap = new JPanel(new BorderLayout());
        orderScrollWrap.setBackground(LGREY);
        orderScrollWrap.add(orderScroll, BorderLayout.CENTER);
        tabContent.add(orderScrollWrap, "ORDERS");

        // ── TAB 3: ĐỊA CHỈ ────────────────────────────────────────────────────
        JPanel addrTab = new JPanel();
        addrTab.setLayout(new BoxLayout(addrTab, BoxLayout.Y_AXIS));
        addrTab.setBackground(LGREY);
        JPanel addrCard = new JPanel();
        addrCard.setLayout(new BoxLayout(addrCard, BoxLayout.Y_AXIS));
        addrCard.setBackground(WHITE);
        addrCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(22, 25, 25, 25)
        ));
        JLabel addrTitle = new JLabel("Địa chỉ giao hàng");
        addrTitle.setFont(new Font("Arial", Font.BOLD, 18));
        addrTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        addrCard.add(addrTitle);
        addrCard.add(Box.createRigidArea(new Dimension(0, 18)));
        String[][] addrs = {
            {"Nhà riêng (mặc định)", "Nguyễn Khách — 0909 123 456\n123 Nguyễn Huệ, P. Bến Nghé, Q.1, TP.HCM"},
            {"Văn phòng",            "Nguyễn Khách — 0909 123 456\n45 Lê Duẩn, P. Bến Nghé, Q.1, TP.HCM"},
        };
        for (String[] a : addrs) {
            JPanel aCard = new JPanel(new BorderLayout(10, 0));
            aCard.setBackground(new Color(250, 250, 250));
            aCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(14, 16, 14, 16)
            ));
            aCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            aCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel aLeft = new JPanel(); aLeft.setLayout(new BoxLayout(aLeft, BoxLayout.Y_AXIS)); aLeft.setOpaque(false);
            JLabel aType = new JLabel(a[0]); aType.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel aDetail = new JLabel("<html><div style='width:340px; color:#555;'>" + a[1].replace("\n","<br>") + "</div></html>");
            aDetail.setFont(new Font("Arial", Font.PLAIN, 13));
            aLeft.add(aType); aLeft.add(Box.createRigidArea(new Dimension(0,4))); aLeft.add(aDetail);
            JButton aEdit = new JButton("Sửa"); aEdit.setFont(new Font("Arial", Font.PLAIN, 12));
            aEdit.setBackground(new Color(240,240,240)); aEdit.setFocusPainted(false);
            aCard.add(aLeft, BorderLayout.CENTER);
            aCard.add(aEdit, BorderLayout.EAST);
            addrCard.add(aCard);
            addrCard.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        JButton addAddrBtn = new JButton("+ Thêm địa chỉ mới");
        addAddrBtn.setBackground(WHITE);
        addAddrBtn.setForeground(RED);
        addAddrBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addAddrBtn.setBorder(new LineBorder(RED, 1));
        addAddrBtn.setFocusPainted(false);
        addAddrBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addAddrBtn.setPreferredSize(new Dimension(200, 40));
        addAddrBtn.setMaximumSize(new Dimension(200, 40));
        addAddrBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addrCard.add(addAddrBtn);
        addrTab.add(addrCard);
        tabContent.add(addrTab, "ADDRESS");

        // ── TAB 4: ĐỔI MẬT KHẨU ─────────────────────────────────────────────
        JPanel passTab = new JPanel(new GridBagLayout());
        passTab.setBackground(LGREY);
        JPanel passCard = new JPanel();
        passCard.setLayout(new BoxLayout(passCard, BoxLayout.Y_AXIS));
        passCard.setBackground(WHITE);
        passCard.setPreferredSize(new Dimension(480, 320));
        passCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 30, 30, 30)
        ));
        JLabel passTitle = new JLabel("Đổi mật khẩu");
        passTitle.setFont(new Font("Arial", Font.BOLD, 18));
        passTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        passCard.add(passTitle);
        passCard.add(Box.createRigidArea(new Dimension(0, 20)));
        String[] passLabels = {"Mật khẩu hiện tại", "Mật khẩu mới", "Xác nhận mật khẩu mới"};
        for (String pl : passLabels) {
            JLabel pLbl = new JLabel(pl);
            pLbl.setFont(new Font("Arial", Font.BOLD, 13));
            pLbl.setForeground(new Color(80,80,80));
            pLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPasswordField pf = new JPasswordField();
            pf.setPreferredSize(new Dimension(400, 38));
            pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            pf.setFont(new Font("Arial", Font.PLAIN, 14));
            pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
            ));
            pf.setAlignmentX(Component.LEFT_ALIGNMENT);
            passCard.add(pLbl);
            passCard.add(Box.createRigidArea(new Dimension(0, 5)));
            passCard.add(pf);
            passCard.add(Box.createRigidArea(new Dimension(0, 14)));
        }
        JButton savePassBtn = new JButton("Lưu mật khẩu");
        savePassBtn.setBackground(RED); savePassBtn.setForeground(WHITE);
        savePassBtn.setFont(new Font("Arial", Font.BOLD, 14));
        savePassBtn.setFocusPainted(false); savePassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        savePassBtn.setBorder(new RoundedBorder(6, RED));
        savePassBtn.setPreferredSize(new Dimension(160, 42));
        savePassBtn.setMaximumSize(new Dimension(160, 42));
        savePassBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        savePassBtn.addActionListener(ev -> JOptionPane.showMessageDialog(this,
            "Mật khẩu đã được cập nhật!", "Thành công", JOptionPane.INFORMATION_MESSAGE));
        passCard.add(savePassBtn);
        passTab.add(passCard);
        tabContent.add(passTab, "PASS");

        // ── Wrap tabContent vào scroll ────────────────────────────────────────
        JScrollPane tabScroll = new JScrollPane(tabContent);
        tabScroll.setBorder(null);
        tabScroll.getVerticalScrollBar().setUnitIncrement(16);
        tabScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        body.add(tabScroll, BorderLayout.CENTER);
        outerWrapper.add(body, BorderLayout.CENTER);
        return outerWrapper;
    }

    // =========================================================================
    // MODULE ADMIN - CONTAINER CHÍNH
    // =========================================================================
    private JPanel createAdminPanel() {
        Color ADMIN_BG    = new Color(15, 23, 42);
        Color ADMIN_SIDE  = new Color(30, 41, 59);
        Color ADMIN_ACC   = new Color(239, 68, 68);
        Color ADMIN_WHITE = new Color(248, 250, 252);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ADMIN_BG);

        // ── SIDEBAR ──────────────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ADMIN_SIDE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Logo vùng admin
        JPanel sideHeader = new JPanel(new BorderLayout());
        sideHeader.setBackground(new Color(20, 30, 50));
        sideHeader.setPreferredSize(new Dimension(220, 64));
        sideHeader.setMaximumSize(new Dimension(220, 64));
        sideHeader.setBorder(new EmptyBorder(14, 18, 14, 18));
        JLabel adminLogo = new JLabel("GEARVN ADMIN");
        adminLogo.setForeground(ADMIN_ACC);
        adminLogo.setFont(new Font("Arial", Font.BOLD, 16));
        sideHeader.add(adminLogo, BorderLayout.CENTER);
        sidebar.add(sideHeader);

        // Content area dùng CardLayout
        CardLayout adminCard = new CardLayout();
        JPanel adminContent = new JPanel(adminCard);
        adminContent.setBackground(ADMIN_BG);
        adminContent.add(createAdminDashboardPanel(), "DASH");
        adminContent.add(createAdminProductPanel(),   "PROD");
        adminContent.add(createAdminCategoryPanel(),  "CAT");
        adminContent.add(createAdminOrderPanel(),     "ORDER");

        // Menu items
        String[][] menuItems = {
            {"📊", "Dashboard",     "DASH"},
            {"📦", "Sản phẩm",     "PROD"},
            {"🏷", "Danh mục",     "CAT"},
            {"📋", "Đơn hàng",     "ORDER"},
        };

        JLabel[] menuLabels = new JLabel[menuItems.length];
        Color SEL_BG = new Color(239, 68, 68, 50);
        Color NOR_FG = new Color(148, 163, 184);
        Color SEL_FG = Color.WHITE;

        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        for (int i = 0; i < menuItems.length; i++) {
            final String key = menuItems[i][2];
            final int idx   = i;

            JPanel item = new JPanel(new BorderLayout());
            item.setBackground(ADMIN_SIDE);
            item.setMaximumSize(new Dimension(220, 46));
            item.setBorder(new EmptyBorder(10, 18, 10, 18));
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(menuItems[i][0]);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            icon.setForeground(NOR_FG);
            JLabel txt = new JLabel(menuItems[i][1]);
            txt.setFont(new Font("Arial", Font.PLAIN, 14));
            txt.setForeground(NOR_FG);
            txt.setBorder(new EmptyBorder(0, 10, 0, 0));

            menuLabels[i] = txt;
            item.add(icon, BorderLayout.WEST);
            item.add(txt, BorderLayout.CENTER);

            item.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    adminCard.show(adminContent, key);
                    for (int j = 0; j < menuLabels.length; j++) {
                        Component p = menuLabels[j].getParent();
                        p.setBackground(ADMIN_SIDE);
                        menuLabels[j].setForeground(NOR_FG);
                        ((JLabel) ((JPanel) p).getComponent(0)).setForeground(NOR_FG);
                    }
                    item.setBackground(SEL_BG);
                    txt.setForeground(SEL_FG);
                    icon.setForeground(SEL_FG);
                }
                public void mouseEntered(MouseEvent e) {
                    if (item.getBackground().equals(ADMIN_SIDE)) item.setBackground(new Color(40, 55, 75));
                }
                public void mouseExited(MouseEvent e) {
                    if (!item.getBackground().equals(SEL_BG)) item.setBackground(ADMIN_SIDE);
                }
            });
            sidebar.add(item);
        }

        // Nút quay lại cửa hàng
        sidebar.add(Box.createVerticalGlue());
        JPanel backItem = new JPanel(new BorderLayout());
        backItem.setBackground(ADMIN_SIDE);
        backItem.setMaximumSize(new Dimension(220, 46));
        backItem.setBorder(new EmptyBorder(10, 18, 10, 18));
        backItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel backIcon = new JLabel("🏠"); backIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); backIcon.setForeground(NOR_FG);
        JLabel backTxt  = new JLabel("Về trang chủ"); backTxt.setFont(new Font("Arial", Font.PLAIN, 14)); backTxt.setForeground(NOR_FG); backTxt.setBorder(new EmptyBorder(0,10,0,0));
        backItem.add(backIcon, BorderLayout.WEST); backItem.add(backTxt, BorderLayout.CENTER);
        backItem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
            public void mouseEntered(MouseEvent e) { backItem.setBackground(new Color(40,55,75)); }
            public void mouseExited(MouseEvent e)  { backItem.setBackground(ADMIN_SIDE); }
        });
        sidebar.add(backItem);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        root.add(sidebar, BorderLayout.WEST);
        root.add(adminContent, BorderLayout.CENTER);
        return root;
    }

    // =========================================================================
    // ADMIN - DASHBOARD TỔNG QUAN
    // =========================================================================
    private JPanel createAdminDashboardPanel() {
        Color BG    = new Color(15, 23, 42);
        Color CARD  = new Color(30, 41, 59);
        Color RED   = new Color(239, 68, 68);
        Color GREEN = new Color(34, 197, 94);
        Color BLUE  = new Color(59, 130, 246);
        Color AMB   = new Color(251, 191, 36);
        Color TXT   = new Color(226, 232, 240);
        Color DIM   = new Color(100, 116, 139);

        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 28, 28, 28));

        // ── Tiêu đề trang ────────────────────────────────────────────────────
        JLabel title = new JLabel("Dashboard Tổng quan");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(title);

        JLabel subtitle = new JLabel("Tháng 5 · 2025  —  dữ liệu cập nhật theo thời gian thực");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(DIM);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(subtitle);
        page.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── 4 KPI CARDS ──────────────────────────────────────────────────────
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiRow.setBackground(BG);
        kpiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        kpiRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        Object[][] kpis = {
            {"Doanh thu tháng",  "4.280.000.000đ", "+12,4%", RED,   "💰"},
            {"Đơn hàng mới",     "1.843",          "+8,1%",  BLUE,  "📋"},
            {"Sản phẩm active",  "362",            "+3",     GREEN, "📦"},
            {"Khách hàng mới",   "549",            "+19,2%", AMB,   "👤"},
        };

        for (Object[] k : kpis) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(16, 18, 16, 18)
            ));

            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setBackground(CARD);
            JLabel lbl = new JLabel((String) k[0]);
            lbl.setFont(new Font("Arial", Font.PLAIN, 12));
            lbl.setForeground(DIM);
            JLabel ico = new JLabel((String) k[4]);
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            topRow.add(lbl, BorderLayout.CENTER);
            topRow.add(ico, BorderLayout.EAST);

            JLabel val = new JLabel((String) k[1]);
            val.setFont(new Font("Arial", Font.BOLD, 20));
            val.setForeground(TXT);

            JLabel delta = new JLabel((String) k[2] + " so với tháng trước");
            delta.setFont(new Font("Arial", Font.PLAIN, 11));
            delta.setForeground((Color) k[3]);

            card.add(topRow);
            card.add(Box.createRigidArea(new Dimension(0, 8)));
            card.add(val);
            card.add(Box.createRigidArea(new Dimension(0, 4)));
            card.add(delta);
            kpiRow.add(card);
        }
        page.add(kpiRow);
        page.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── BIỂU ĐỒ DOANH THU (vẽ tay bằng Graphics) ────────────────────────
        JPanel chartCard = new JPanel(new BorderLayout());
        chartCard.setBackground(CARD);
        chartCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(51,65,85), 1),
            new EmptyBorder(18, 20, 18, 20)
        ));
        chartCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        chartCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel chartTitle = new JLabel("Doanh thu 6 tháng gần nhất  (triệu VNĐ)");
        chartTitle.setFont(new Font("Arial", Font.BOLD, 14));
        chartTitle.setForeground(TXT);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        // Canvas vẽ cột
        int[] revenues = {2800, 3100, 2650, 3900, 4100, 4280};
        String[] months = {"T12/24", "T1/25", "T2/25", "T3/25", "T4/25", "T5/25"};
        JPanel chartCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int padL = 40, padR = 20, padT = 20, padB = 30;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;
                int maxVal = 5000;
                int bars   = revenues.length;
                int gap    = 14;
                int barW   = (chartW - gap * (bars + 1)) / bars;

                // Grid lines
                g2.setColor(new Color(51, 65, 85));
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{4,4}, 0));
                for (int line = 0; line <= 4; line++) {
                    int y = padT + chartH - (int)(chartH * line / 4.0);
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(new Color(100,116,139));
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    g2.drawString(String.valueOf(maxVal * line / 4), 0, y + 4);
                    g2.setColor(new Color(51,65,85));
                }

                // Bars với gradient
                for (int i = 0; i < bars; i++) {
                    int x = padL + gap + i * (barW + gap);
                    int barH = (int)(chartH * revenues[i] / (double) maxVal);
                    int y = padT + chartH - barH;

                    GradientPaint gp = new GradientPaint(x, y, new Color(239,68,68), x, padT + chartH, new Color(239,68,68,80));
                    g2.setPaint(gp);
                    g2.setStroke(new BasicStroke(1));
                    g2.fillRoundRect(x, y, barW, barH, 4, 4);

                    // Value label on top
                    g2.setColor(new Color(226,232,240));
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString(revenues[i] + "M", x + barW/2 - 16, y - 5);

                    // Month label
                    g2.setColor(new Color(100,116,139));
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    g2.drawString(months[i], x + barW/2 - 20, padT + chartH + 18);
                }
            }
        };
        chartCanvas.setBackground(CARD);
        chartCard.add(chartCanvas, BorderLayout.CENTER);
        page.add(chartCard);
        page.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── BẢNG ĐƠN HÀNG GẦN ĐÂY ───────────────────────────────────────────
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(51,65,85), 1),
            new EmptyBorder(16, 18, 16, 18)
        ));
        tableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tblTitle = new JLabel("Đơn hàng gần đây");
        tblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tblTitle.setForeground(TXT);
        tableCard.add(tblTitle, BorderLayout.NORTH);

        String[] colNames = {"Mã đơn", "Khách hàng", "Sản phẩm", "Tổng tiền", "Trạng thái"};
        Object[][] data = {
            {"#GVN-2501", "Nguyễn Văn An",   "Laptop ASUS ROG Strix",    "31.790.000đ", "Đã giao"},
            {"#GVN-2502", "Trần Thị Bình",   "Chuột Razer DeathAdder",   "1.590.000đ",  "Đang giao"},
            {"#GVN-2503", "Lê Minh Châu",    "Bàn phím AULA F75",        "650.000đ",    "Đã giao"},
            {"#GVN-2504", "Phạm Thanh Dung", "Tai nghe HyperX Cloud II", "2.890.000đ",  "Chờ xác nhận"},
            {"#GVN-2505", "Hoàng Văn Em",    "Laptop MSI Katana",        "22.500.000đ", "Đã hủy"},
        };

        JTable recentTable = new JTable(data, colNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? CARD : new Color(40, 54, 72));
                c.setForeground(TXT);
                if (col == 4) {
                    String val = getValueAt(row, col).toString();
                    if (val.equals("Đã giao"))         c.setForeground(GREEN);
                    else if (val.equals("Đang giao"))  c.setForeground(BLUE);
                    else if (val.equals("Đã hủy"))     c.setForeground(RED);
                    else                                c.setForeground(AMB);
                }
                return c;
            }
        };
        styleAdminTable(recentTable, CARD, DIM, TXT);
        JScrollPane sp = new JScrollPane(recentTable);
        sp.setBorder(null);
        sp.getViewport().setBackground(CARD);
        tableCard.add(sp, BorderLayout.CENTER);
        page.add(tableCard);

        JScrollPane pageScroll = new JScrollPane(page);
        pageScroll.setBorder(null);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);
        pageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.getViewport().setBackground(BG);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(pageScroll);
        return wrap;
    }

    // =========================================================================
    // ADMIN - QUẢN LÝ SẢN PHẨM (CRUD)
    // =========================================================================
    private JPanel createAdminProductPanel() {
        Color BG   = new Color(15, 23, 42);
        Color CARD = new Color(30, 41, 59);
        Color RED  = new Color(239, 68, 68);
        Color TXT  = new Color(226, 232, 240);
        Color DIM  = new Color(100, 116, 139);
        Color BORDER_C = new Color(51, 65, 85);

        // ── Dữ liệu mẫu ─────────────────────────────────────────────────────
        String[] colNames = {"ID", "Tên sản phẩm", "Danh mục", "Giá bán", "Tồn kho", "Trạng thái"};
        Object[][] data = {
            {1, "Laptop ASUS ROG Strix G16", "Laptop Gaming",    "31.790.000đ", 15, "Đang bán"},
            {2, "Laptop MSI Katana 15",      "Laptop Gaming",    "22.500.000đ", 8,  "Đang bán"},
            {3, "Chuột Razer DeathAdder V3", "Chuột Gaming",     "1.590.000đ",  42, "Đang bán"},
            {4, "Chuột Logitech G502 X",     "Chuột Gaming",     "1.290.000đ",  30, "Đang bán"},
            {5, "Bàn phím AULA F75",         "Bàn Phím Gaming",  "650.000đ",    67, "Đang bán"},
            {6, "Bàn phím Akko 3098B",       "Bàn Phím Gaming",  "1.250.000đ",  0,  "Hết hàng"},
            {7, "Tai nghe Razer Barracuda",  "Tai Nghe Gaming",  "2.890.000đ",  20, "Đang bán"},
            {8, "Tai nghe HyperX Cloud II",  "Tai Nghe Gaming",  "2.490.000đ",  5,  "Đang bán"},
        };
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, colNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28, 28, 28, 28));

        // ── Thanh tiêu đề + nút Thêm ─────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));
        JLabel title = new JLabel("Quản lý Sản phẩm");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TXT);
        JButton addBtn = buildAdminBtn("+ Thêm sản phẩm", RED, Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Bảng sản phẩm ────────────────────────────────────────────────────
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? CARD : new Color(40, 54, 72));
                c.setForeground(TXT);
                if (col == 5) {
                    String v = getValueAt(row, col).toString();
                    c.setForeground(v.equals("Hết hàng") ? RED : new Color(34,197,94));
                }
                return c;
            }
        };
        styleAdminTable(table, CARD, DIM, TXT);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(new LineBorder(BORDER_C, 1));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.getViewport().setBackground(CARD);
        tableCard.add(sp, BorderLayout.CENTER);

        // ── Toolbar hành động ────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(20, 30, 50));
        toolbar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JButton editBtn   = buildAdminBtn("✏ Sửa",  new Color(59,130,246), Color.WHITE);
        JButton deleteBtn = buildAdminBtn("🗑 Xóa",  new Color(239,68,68),  Color.WHITE);
        JButton toggleBtn = buildAdminBtn("⟳ Trạng thái", new Color(100,116,139), Color.WHITE);

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Vui lòng chọn sản phẩm!"); return; }
            showProductFormDialog(model, row, colNames);
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Vui lòng chọn sản phẩm!"); return; }
            int confirm = JOptionPane.showConfirmDialog(null,
                "Xóa sản phẩm \"" + model.getValueAt(row,1) + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) model.removeRow(row);
        });

        toggleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Vui lòng chọn sản phẩm!"); return; }
            String cur = model.getValueAt(row, 5).toString();
            model.setValueAt(cur.equals("Đang bán") ? "Hết hàng" : "Đang bán", row, 5);
        });

        addBtn.addActionListener(e -> showProductFormDialog(model, -1, colNames));

        toolbar.add(editBtn); toolbar.add(deleteBtn); toolbar.add(toggleBtn);
        tableCard.add(toolbar, BorderLayout.SOUTH);

        root.add(tableCard, BorderLayout.CENTER);

        JScrollPane rootScroll = new JScrollPane(root);
        rootScroll.setBorder(null);
        rootScroll.getVerticalScrollBar().setUnitIncrement(16);
        rootScroll.getViewport().setBackground(BG);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(rootScroll);
        return wrap;
    }

    private void showProductFormDialog(javax.swing.table.DefaultTableModel model, int editRow, String[] cols) {
        JDialog dlg = new JDialog((JFrame) SwingUtilities.getWindowAncestor(mainContentPanel),
            editRow < 0 ? "Thêm sản phẩm mới" : "Sửa sản phẩm", true);
        dlg.setSize(480, 400);
        dlg.setLocationRelativeTo(mainContentPanel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 41, 59));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        String[] fieldLabels = {"Tên sản phẩm", "Danh mục", "Giá bán", "Tồn kho"};
        int[]    colIdxs     = {1, 2, 3, 4};
        JTextField[] fields  = new JTextField[fieldLabels.length];

        Color TXT  = new Color(226,232,240);
        Color DIM  = new Color(100,116,139);
        Color CARD = new Color(30,41,59);

        for (int i = 0; i < fieldLabels.length; i++) {
            JLabel lbl = new JLabel(fieldLabels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(DIM);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lbl);
            panel.add(Box.createRigidArea(new Dimension(0,4)));

            JTextField tf = new JTextField(editRow >= 0 ? model.getValueAt(editRow, colIdxs[i]).toString() : "");
            tf.setFont(new Font("Arial", Font.PLAIN, 14));
            tf.setBackground(new Color(15,23,42));
            tf.setForeground(TXT);
            tf.setCaretColor(TXT);
            tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(51,65,85),1),
                new EmptyBorder(6,10,6,10)
            ));
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            tf.setAlignmentX(Component.LEFT_ALIGNMENT);
            fields[i] = tf;
            panel.add(tf);
            panel.add(Box.createRigidArea(new Dimension(0,12)));
        }

        JButton saveBtn = buildAdminBtn("Lưu", new Color(239,68,68), Color.WHITE);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(ev -> {
            if (editRow < 0) {
                int newId = model.getRowCount() + 1;
                model.addRow(new Object[]{newId, fields[0].getText(), fields[1].getText(),
                    fields[2].getText(), fields[3].getText(), "Đang bán"});
            } else {
                for (int i = 0; i < colIdxs.length; i++)
                    model.setValueAt(fields[i].getText(), editRow, colIdxs[i]);
            }
            dlg.dispose();
        });
        panel.add(saveBtn);
        dlg.setContentPane(panel);
        dlg.setVisible(true);
    }

    // =========================================================================
    // ADMIN - QUẢN LÝ DANH MỤC (CRUD)
    // =========================================================================
    private JPanel createAdminCategoryPanel() {
        Color BG     = new Color(15, 23, 42);
        Color CARD   = new Color(30, 41, 59);
        Color RED    = new Color(239, 68, 68);
        Color TXT    = new Color(226, 232, 240);
        Color DIM    = new Color(100, 116, 139);
        Color BORDER_C = new Color(51, 65, 85);

        String[] cols = {"ID", "Tên danh mục", "Mô tả", "Số SP", "Hiển thị"};
        Object[][] data = {
            {1, "Laptop Gaming",    "Laptop gaming hiệu năng cao",  120, "Có"},
            {2, "Chuột Gaming",     "Chuột gaming nhiều DPI",       85,  "Có"},
            {3, "Bàn Phím Gaming",  "Bàn phím cơ và membrane",      63,  "Có"},
            {4, "Tai Nghe Gaming",  "Tai nghe có mic, surround 7.1",40,  "Có"},
            {5, "Màn Hình Gaming",  "Màn hình tốc độ cao 144Hz+",   34,  "Có"},
            {6, "Linh Kiện PC",     "RAM, SSD, VGA, CPU...",         20,  "Ẩn"},
        };
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(0,0,18,0));
        JLabel title = new JLabel("Quản lý Danh mục");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TXT);
        JButton addBtn = buildAdminBtn("+ Thêm danh mục", RED, Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? CARD : new Color(40,54,72));
                c.setForeground(TXT);
                if (col == 4) c.setForeground(getValueAt(row,col).toString().equals("Có") ?
                    new Color(34,197,94) : new Color(239,68,68));
                return c;
            }
        };
        styleAdminTable(table, CARD, DIM, TXT);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(new LineBorder(BORDER_C,1));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null); sp.getViewport().setBackground(CARD);
        tableCard.add(sp, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        toolbar.setBackground(new Color(20,30,50));
        JButton editBtn  = buildAdminBtn("✏ Sửa",  new Color(59,130,246), Color.WHITE);
        JButton delBtn   = buildAdminBtn("🗑 Xóa",  RED, Color.WHITE);
        JButton visBtn   = buildAdminBtn("👁 Hiện/Ẩn", new Color(100,116,139), Color.WHITE);

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Chọn danh mục trước!"); return; }
            String newName = JOptionPane.showInputDialog(null, "Tên danh mục:", model.getValueAt(row,1));
            if (newName != null && !newName.isBlank()) model.setValueAt(newName, row, 1);
        });
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Chọn danh mục trước!"); return; }
            if (JOptionPane.showConfirmDialog(null,"Xóa danh mục \""+model.getValueAt(row,1)+"\"?",
                "Xác nhận",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) model.removeRow(row);
        });
        visBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Chọn danh mục trước!"); return; }
            model.setValueAt(model.getValueAt(row,4).equals("Có")?"Ẩn":"Có", row, 4);
        });
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(null,"Tên danh mục mới:");
            if (name != null && !name.isBlank()) {
                String desc = JOptionPane.showInputDialog(null,"Mô tả:");
                model.addRow(new Object[]{model.getRowCount()+1, name, desc!=null?desc:"", 0,"Có"});
            }
        });
        toolbar.add(editBtn); toolbar.add(delBtn); toolbar.add(visBtn);
        tableCard.add(toolbar, BorderLayout.SOUTH);
        root.add(tableCard, BorderLayout.CENTER);

        JScrollPane rootScroll = new JScrollPane(root);
        rootScroll.setBorder(null);
        rootScroll.getVerticalScrollBar().setUnitIncrement(16);
        rootScroll.getViewport().setBackground(BG);
        JPanel wrap = new JPanel(new BorderLayout()); wrap.add(rootScroll);
        return wrap;
    }

    // =========================================================================
    // ADMIN - QUẢN LÝ ĐƠN HÀNG
    // =========================================================================
    private JPanel createAdminOrderPanel() {
        Color BG     = new Color(15, 23, 42);
        Color CARD   = new Color(30, 41, 59);
        Color TXT    = new Color(226, 232, 240);
        Color DIM    = new Color(100, 116, 139);
        Color GREEN  = new Color(34, 197, 94);
        Color BLUE   = new Color(59, 130, 246);
        Color RED    = new Color(239, 68, 68);
        Color AMB    = new Color(251, 191, 36);
        Color BORDER_C = new Color(51, 65, 85);

        String[] cols = {"Mã đơn", "Khách hàng", "Ngày đặt", "Sản phẩm", "Tổng tiền", "Trạng thái"};
        Object[][] data = {
            {"#GVN-2501","Nguyễn Văn An",  "01/05/2025","Laptop ASUS ROG Strix",    "31.790.000đ","Đã giao"},
            {"#GVN-2502","Trần Thị Bình",  "02/05/2025","Chuột Razer DeathAdder",   "1.590.000đ", "Đang giao"},
            {"#GVN-2503","Lê Minh Châu",   "02/05/2025","Bàn phím AULA F75",        "650.000đ",   "Đã giao"},
            {"#GVN-2504","Phạm Thanh Dung","03/05/2025","Tai nghe HyperX Cloud II", "2.890.000đ", "Chờ xác nhận"},
            {"#GVN-2505","Hoàng Văn Em",   "03/05/2025","Laptop MSI Katana",        "22.500.000đ","Đã hủy"},
            {"#GVN-2506","Vũ Thị Phương",  "04/05/2025","Chuột Logitech G502 X",   "1.290.000đ", "Đang giao"},
            {"#GVN-2507","Đặng Minh Quân", "04/05/2025","Bàn phím Akko 3098B",     "1.250.000đ", "Chờ xác nhận"},
            {"#GVN-2508","Bùi Lan Anh",    "05/05/2025","Tai nghe Razer Barracuda","2.890.000đ", "Chờ xác nhận"},
        };
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28,28,28,28));

        // ── Tiêu đề + bộ lọc ─────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(0,0,18,0));

        JLabel title = new JLabel("Quản lý Đơn hàng");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TXT);

        String[] statuses = {"Tất cả", "Chờ xác nhận", "Đang giao", "Đã giao", "Đã hủy"};
        JComboBox<String> filterBox = new JComboBox<>(statuses);
        filterBox.setBackground(CARD);
        filterBox.setForeground(TXT);
        filterBox.setFont(new Font("Arial", Font.PLAIN, 13));
        filterBox.setPreferredSize(new Dimension(160, 36));

        JPanel filterArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterArea.setBackground(BG);
        JLabel filterLbl = new JLabel("Lọc: ");
        filterLbl.setForeground(DIM);
        filterLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        filterArea.add(filterLbl);
        filterArea.add(filterBox);

        header.add(title, BorderLayout.WEST);
        header.add(filterArea, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Bảng đơn hàng ────────────────────────────────────────────────────
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? CARD : new Color(40,54,72));
                c.setForeground(TXT);
                if (col == 5) {
                    String v = getValueAt(row,col).toString();
                    if (v.equals("Đã giao"))         c.setForeground(GREEN);
                    else if (v.equals("Đang giao"))  c.setForeground(BLUE);
                    else if (v.equals("Đã hủy"))     c.setForeground(RED);
                    else                              c.setForeground(AMB);
                }
                return c;
            }
        };
        styleAdminTable(table, CARD, DIM, TXT);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(new LineBorder(BORDER_C,1));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null); sp.getViewport().setBackground(CARD);
        tableCard.add(sp, BorderLayout.CENTER);

        // ── Toolbar cập nhật trạng thái ──────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        toolbar.setBackground(new Color(20,30,50));

        JButton confirmBtn = buildAdminBtn("✔ Xác nhận",   new Color(34,197,94), Color.WHITE);
        JButton shipBtn    = buildAdminBtn("🚚 Đang giao",  BLUE, Color.WHITE);
        JButton doneBtn    = buildAdminBtn("✅ Đã giao",    new Color(34,197,94), Color.WHITE);
        JButton cancelBtn  = buildAdminBtn("✕ Hủy đơn",    RED, Color.WHITE);
        JButton detailBtn  = buildAdminBtn("🔍 Chi tiết",   new Color(100,116,139), Color.WHITE);

        confirmBtn.addActionListener(e -> setOrderStatus(table, model, "Đang giao"));
        shipBtn   .addActionListener(e -> setOrderStatus(table, model, "Đang giao"));
        doneBtn   .addActionListener(e -> setOrderStatus(table, model, "Đã giao"));
        cancelBtn .addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Chọn đơn hàng trước!"); return; }
            if (JOptionPane.showConfirmDialog(null,"Xác nhận hủy đơn "+model.getValueAt(row,0)+"?",
                "Hủy đơn",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                model.setValueAt("Đã hủy", row, 5);
        });
        detailBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Chọn đơn hàng trước!"); return; }
            JOptionPane.showMessageDialog(null,
                "Mã đơn  : " + model.getValueAt(row,0) + "\n" +
                "Khách   : " + model.getValueAt(row,1) + "\n" +
                "Ngày    : " + model.getValueAt(row,2) + "\n" +
                "SP      : " + model.getValueAt(row,3) + "\n" +
                "Tổng    : " + model.getValueAt(row,4) + "\n" +
                "TT      : " + model.getValueAt(row,5),
                "Chi tiết đơn hàng", JOptionPane.INFORMATION_MESSAGE);
        });

        // Lọc theo trạng thái
        filterBox.addActionListener(e -> {
            String sel = filterBox.getSelectedItem().toString();
            model.setRowCount(0);
            for (Object[] row : data) {
                if (sel.equals("Tất cả") || row[5].equals(sel)) model.addRow(row);
            }
        });

        toolbar.add(confirmBtn); toolbar.add(shipBtn); toolbar.add(doneBtn);
        toolbar.add(cancelBtn);  toolbar.add(detailBtn);
        tableCard.add(toolbar, BorderLayout.SOUTH);
        root.add(tableCard, BorderLayout.CENTER);

        JScrollPane rootScroll = new JScrollPane(root);
        rootScroll.setBorder(null);
        rootScroll.getVerticalScrollBar().setUnitIncrement(16);
        rootScroll.getViewport().setBackground(BG);
        JPanel wrap = new JPanel(new BorderLayout()); wrap.add(rootScroll);
        return wrap;
    }

    private void setOrderStatus(JTable table, javax.swing.table.DefaultTableModel model, String status) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null,"Chọn đơn hàng trước!"); return; }
        model.setValueAt(status, row, 5);
    }

    // ── Helper: style chung cho JTable trong Admin ────────────────────────────
    private void styleAdminTable(JTable table, Color bg, Color headerFg, Color cellFg) {
        table.setBackground(bg);
        table.setForeground(cellFg);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(239, 68, 68, 80));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(20, 30, 50));
        table.getTableHeader().setForeground(headerFg);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBorder(new EmptyBorder(8, 8, 8, 8));
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            { setOpaque(true); setBorder(new EmptyBorder(0, 12, 0, 12)); }
        });
    }

    // ── Helper: tạo nút Admin đồng nhất ─────────────────────────────────────
    private JButton buildAdminBtn(String label, Color bg, Color fg) {
        JButton btn = new JButton(label);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bg.darker(), 1),
            new EmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GearVNApp().setVisible(true);
        });
    }
}
