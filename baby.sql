-- 사용자 정보 테이블
CREATE TABLE Users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 사용자 고유 ID (자동 증가, 기본 키)
    email VARCHAR(255) NOT NULL UNIQUE,       -- 사용자 이메일 (고유, 필수 입력)
    password VARCHAR(255) NOT NULL,           -- 비밀번호 (해시 처리된 값)
    name VARCHAR(100) NOT NULL,               -- 사용자 이름
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP -- 계정 생성 시간
);

-- 아기 정보 테이블
CREATE TABLE Babies (
    baby_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 아기 고유 ID (자동 증가, 기본 키)
    user_id BIGINT NOT NULL,                   -- 사용자 ID (Users 테이블의 외래 키)
    baby_name VARCHAR(100) NOT NULL,           -- 아기 이름
    birth_date DATE NOT NULL,                  -- 아기 생년월일
    gender ENUM('MALE', 'FEMALE') NOT NULL,    -- 성별 (남/여)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 아기 등록 시간
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE -- 사용자 삭제 시 아기 정보도 삭제
);

-- 아기 상태 기록 테이블
CREATE TABLE BabyStates (
    state_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 상태 고유 ID (자동 증가, 기본 키)
    baby_id BIGINT NOT NULL,                    -- 아기 ID (Babies 테이블의 외래 키)
    state_type ENUM('Fall', 'Cry', 'Choking') NOT NULL, -- 상태 유형
    state_details TEXT,                         -- 상태 세부 정보 (예: 울음소리 분석 결과)
    detected_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 상태 감지 시간
    FOREIGN KEY (baby_id) REFERENCES Babies(baby_id) ON DELETE CASCADE -- 아기 삭제 시 상태 기록도 삭제
);

-- 아기 상태 통계 테이블
CREATE TABLE BabyStatistics (
    stat_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 통계 고유 ID (자동 증가, 기본 키)
    baby_id BIGINT NOT NULL,                   -- 아기 ID (Babies 테이블의 외래 키)
    start_date DATE NOT NULL,                  -- 통계 시작 날짜
    end_date DATE NOT NULL,                    -- 통계 종료 날짜
    fall_count INT DEFAULT 0,                  -- 낙상 발생 횟수
    cry_count INT DEFAULT 0,                   -- 울음 감지 횟수
    choking_count INT DEFAULT 0,               -- 질식 경고 횟수
    FOREIGN KEY (baby_id) REFERENCES Babies(baby_id) ON DELETE CASCADE -- 아기 삭제 시 통계 삭제
);

-- 부모 연락처 정보 테이블
CREATE TABLE ParentContacts (
    contact_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 연락처 고유 ID (자동 증가, 기본 키)
    baby_id BIGINT NOT NULL,                      -- 아기 ID (Babies 테이블의 외래 키)
    mom_phone_number VARCHAR(15) NOT NULL,        -- 엄마 전화번호
    dad_phone_number VARCHAR(15) NOT NULL,        -- 아빠 전화번호
    FOREIGN KEY (baby_id) REFERENCES Babies(baby_id) ON DELETE CASCADE -- 아기 삭제 시 부모 정보도 삭제
);

-- 알림 기록 테이블
CREATE TABLE Notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 알림 고유 ID (자동 증가, 기본 키)
    baby_id BIGINT NOT NULL,                           -- 아기 ID (Babies 테이블의 외래 키)
    state_id BIGINT,                                   -- 상태 ID (BabyStates 테이블의 외래 키)
    contact_id BIGINT,                                 -- 부모 연락처 ID (ParentContacts 테이블의 외래 키)
    notification_type ENUM('SMS', 'CALL', 'PUSH') NOT NULL, -- 알림 유형
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,        -- 알림 전송 시간
    status ENUM('PENDING', 'SENT', 'FAILED') DEFAULT 'PENDING', -- 알림 상태
    FOREIGN KEY (baby_id) REFERENCES Babies(baby_id) ON DELETE CASCADE, -- 아기 삭제 시 알림 삭제
    FOREIGN KEY (state_id) REFERENCES BabyStates(state_id) ON DELETE SET NULL, -- 상태 삭제 시 알림 상태 NULL
    FOREIGN KEY (contact_id) REFERENCES ParentContacts(contact_id) ON DELETE SET NULL -- 부모 삭제 시 연락처 NULL
);