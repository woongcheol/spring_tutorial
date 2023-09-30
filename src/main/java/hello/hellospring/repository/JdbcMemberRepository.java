package hello.hellospring.repository;
import hello.hellospring.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository{

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)"; // SQL 쿼리문 작성
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection(); // DB 연결 수립
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // PreparedStatement 생성 및 자동 생성 키 반환 설정
            pstmt.setString(1, member.getName()); // SQL 문의 첫 번째 파라미터로 멤버 이름 설정
            pstmt.executeUpdate();  // SQL 문 실행

            rs = pstmt.getGeneratedKeys();  // 자동 생성된 키 값을 가져옴

            if (rs.next()) {
                member.setId(rs.getLong(1));  // 멤버 객체의 ID를 설정함
            } else {
                throw new SQLException("id 조회 실패");  // ID 조회에 실패하면 예외 발생시킴
            }

            return member;  // 저장된 멤버 객체 반환
        } catch (Exception e) {
            throw new IllegalStateException(e);   // 예외 발생 시 상태 예외를 던짐
        } finally {
            close(conn, pstmt, rs);   // 사용한 리소스 해제
        }
    }


    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        } }


    @Override
    public List<Member> findAll() {
        String sql = "select * from member";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                members.add(member);
            }
            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }


    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);   // 데이터 소스로부터 커넥션을 얻어옴
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) // DB 연결 해제 메서드

    {
        try {
            if (rs != null) {
                rs.close(); // 결과셋이 널이 아니면 닫음
            }
        } catch (SQLException e) {
            e.printStackTrace(); // PreparedStatement가 널이 아니면 닫음
        } try {
        if (pstmt != null) {
            pstmt.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
        try {
            if (conn != null) {
                close(conn); // 커넥션이 널이 아니면 닫음
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource); // 데이터 소스를 이용하여 커넥션을 해제

    } }

