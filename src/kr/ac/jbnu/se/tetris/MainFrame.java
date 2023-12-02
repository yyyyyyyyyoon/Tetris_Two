package kr.ac.jbnu.se.tetris;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Scanner;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JButton startButton;
    private JButton loginButton;

    private JPanel settingsPanel = new JPanel();
    private int initialVolume = 50; //초기 음량
    private int volume = initialVolume;
    private JSlider volumeSlider;

    private JTextField usernameField; // 유저 이름 입력 필드
    private JPasswordField passwordField; // 비밀번호 입력 필드
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainFrame() {
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("C:\\Users\\USER\\Desktop\\수업 자료\\2-2\\소스코드분석\\배경이미지.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        setContentPane(mainPanel);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel startPanel = new JPanel();
        startButton = new JButton("Sign up");
        loginButton = new JButton("Login");
        usernameField = new JTextField(20); // 유저 이름 입력 필드
        passwordField = new JPasswordField(20); // 비밀번호 입력 필드
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 유저 이름과 비밀번호를 가져와서 사용
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // 회원가입 또는 로그인 로직
                try {
                    // 회원가입 또는 로그인 로직
                    add_user(username, password);
                    // 알림창
                    JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    // user, password 필드 비우기
                    usernameField.setText("");
                    passwordField.setText("");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the entered username and password
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Implement your login logic here (compare with database data)
//                try {
//                    // 로그인 로직
//                    if (isValidLogin(username, password)) {
                        cardLayout.show(cardPanel, "levelSelection");
//                    } else {
//                        // Show an error message or take other actions for invalid login
//                        JOptionPane.showMessageDialog(null, "계정정보 및 비밀번호가 일치하지 않습니다.", "경고", JOptionPane.WARNING_MESSAGE);
//                    }
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }

                // If login is successful, transition to the level selection screen

            }
        });


        startPanel.add(new JLabel("Username: "));
        startPanel.add(usernameField);
        startPanel.add(new JLabel("Password: "));
        startPanel.add(passwordField);
        startPanel.add(startButton);
        startPanel.add(loginButton);


        //환경설정 버튼
        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "settings");
            }
        });
        startPanel.add(settingsButton);

        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, volume);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        settingsPanel.add(volumeSlider);

        JButton saveButton = new JButton("Save");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "start");
            }
        });
        settingsPanel.add(saveButton);

        cardPanel.add(settingsPanel, "settings");

        // 슬라이더 변경 이벤트 리스너
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    volume = source.getValue();
                    float normalizedVolume = (float) volume / 100.0f;
                    Music.setVolume(normalizedVolume);
                }
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "settings");
            }
        });


        //level1~5
        JPanel levelSelectionPanel = new JPanel();
        for (int i = 1; i <= 5; i++) {
            JButton levelButton = new JButton("Level" + i);
            levelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Tetris tetris = new Tetris();
                    tetris.setLocationRelativeTo(null);
                    tetris.setVisible(true);
                    dispose();
                }
            });
            levelSelectionPanel.add(levelButton);
        }

        cardPanel.add(startPanel, "start");
        cardPanel.add(levelSelectionPanel, "levelSelection");

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(20, 0, 0, 0);

        mainPanel.add(cardPanel);
        cardLayout.show(cardPanel, "start");
    }



    public static void main(String[] args){
        Music.playBackgroundMusic("C:\\Users\\USER\\Downloads\\BGM-Tetris-Kalinka_1.wav");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }

    static void add_user(String username, String password) throws SQLException{
        // SQL URL과 mysql 유저 정보
        String SQLpassword = "0000";
        String SQLName = "root";
        String url = "jdbc:mysql://localhost:3306/jdbc";

        // SQL 연결
        Connection connection = DriverManager.getConnection(url, SQLName, SQLpassword);
        // Statement: SQL 쿼리를 실행하기 위한 인터페이스입니다
        Statement statement = connection.createStatement();

        // 테이블에 유저를 추가합니다.
        // sql insert 문
        String sql = "INSERT INTO user_password (Name, Password) VALUES (\""+username+"\", \""+password+"\")";
        // insert 문 실행
        statement.executeUpdate(sql);
    }

    static boolean isValidLogin(String username, String password) throws SQLException{
        // SQL URL과 mysql 유저 정보
        String SQLpassword = "0000";
        String SQLName = "root";
        String url = "jdbc:mysql://localhost:3306/jdbc";

        // SQL 연결
        Connection connection = DriverManager.getConnection(url, SQLName, SQLpassword);
        // Statement: SQL 쿼리를 실행하기 위한 인터페이스입니다
        try {
            String sql = "SELECT * FROM user_password WHERE Name = ? AND Password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // true if a row was found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database connection or query errors here
        }
        return false; // Default to false in case of errors
    }
}
