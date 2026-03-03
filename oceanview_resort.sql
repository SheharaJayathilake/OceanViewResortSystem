-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 01, 2026 at 05:28 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `oceanview_resort`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_calculate_bill` (IN `p_reservation_id` INT, OUT `p_total_amount` DECIMAL(10,2), OUT `p_number_of_nights` INT)   BEGIN
    DECLARE v_check_in DATE;
    DECLARE v_check_out DATE;
    DECLARE v_rate DECIMAL(10,2);
    
    SELECT r.check_in_date, r.check_out_date, rt.rate_per_night
    INTO v_check_in, v_check_out, v_rate
    FROM reservations r
    JOIN room_types rt ON r.room_type_id = rt.room_type_id
    WHERE r.reservation_id = p_reservation_id;
    
    SET p_number_of_nights = DATEDIFF(v_check_out, v_check_in);
    SET p_total_amount = p_number_of_nights * v_rate;
    
    UPDATE reservations 
    SET total_amount = p_total_amount 
    WHERE reservation_id = p_reservation_id;
END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `fn_check_availability` (`p_room_type_id` INT, `p_check_in` DATE, `p_check_out` DATE) RETURNS TINYINT(1) DETERMINISTIC BEGIN
    DECLARE v_conflict_count INT;
    
    SELECT COUNT(*) INTO v_conflict_count
    FROM reservations
    WHERE room_type_id = p_room_type_id
    AND status IN ('CONFIRMED', 'CHECKED_IN')
    AND (
        (check_in_date <= p_check_in AND check_out_date > p_check_in)
        OR (check_in_date < p_check_out AND check_out_date >= p_check_out)
        OR (check_in_date >= p_check_in AND check_out_date <= p_check_out)
    );
    
    RETURN v_conflict_count = 0;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

CREATE TABLE `audit_log` (
  `log_id` int(11) NOT NULL,
  `table_name` varchar(50) NOT NULL,
  `operation` varchar(20) NOT NULL,
  `record_id` int(11) NOT NULL,
  `old_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`old_value`)),
  `new_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`new_value`)),
  `changed_by` int(11) DEFAULT NULL,
  `changed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `audit_log`
--

INSERT INTO `audit_log` (`log_id`, `table_name`, `operation`, `record_id`, `old_value`, `new_value`, `changed_by`, `changed_at`) VALUES
(1, 'reservations', 'UPDATE', 1, '{\"status\": \"PENDING\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:42:48'),
(2, 'reservations', 'UPDATE', 1, '{\"status\": \"CONFIRMED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:43:17'),
(3, 'reservations', 'UPDATE', 1, '{\"status\": \"CONFIRMED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CANCELLED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:45:07'),
(4, 'reservations', 'UPDATE', 1, '{\"status\": \"CANCELLED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CANCELLED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:45:09'),
(5, 'reservations', 'UPDATE', 2, '{\"status\": \"PENDING\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:46:07'),
(6, 'reservations', 'UPDATE', 2, '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:48:25'),
(7, 'reservations', 'UPDATE', 2, '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:49:00'),
(8, 'reservations', 'UPDATE', 2, '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:49:39'),
(9, 'reservations', 'UPDATE', 2, '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 1800.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:49:44'),
(10, 'reservations', 'UPDATE', 3, '{\"status\": \"PENDING\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:51:20'),
(11, 'reservations', 'UPDATE', 3, '{\"status\": \"CONFIRMED\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:51:22'),
(12, 'reservations', 'UPDATE', 3, '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-19 17:53:41'),
(13, 'reservations', 'UPDATE', 4, '{\"status\": \"PENDING\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-21 07:40:50'),
(14, 'reservations', 'UPDATE', 4, '{\"status\": \"CONFIRMED\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-21 07:41:24'),
(15, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-21 07:41:30'),
(16, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-21 07:47:03'),
(17, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-21 10:06:46'),
(18, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-21 10:07:01'),
(19, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-24 19:03:08'),
(20, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:03:08'),
(21, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:03:09'),
(22, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:03:25'),
(23, 'reservations', 'UPDATE', 3, '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-24 19:03:40'),
(24, 'reservations', 'UPDATE', 3, '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:03:40'),
(25, 'reservations', 'UPDATE', 3, '{\"status\": \"CHECKED_IN\", \"total_amount\": 300.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 300.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:03:46'),
(26, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_IN\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:51:07'),
(27, 'reservations', 'UPDATE', 4, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 250.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:51:11'),
(28, 'reservations', 'UPDATE', 5, '{\"status\": \"PENDING\", \"total_amount\": 750.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 750.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-24 19:52:18'),
(29, 'reservations', 'UPDATE', 5, '{\"status\": \"CONFIRMED\", \"total_amount\": 750.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CANCELLED\", \"total_amount\": 750.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-24 19:52:53'),
(30, 'reservations', 'UPDATE', 6, '{\"status\": \"PENDING\", \"total_amount\": 1000.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 1000.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-24 19:53:19'),
(31, 'reservations', 'UPDATE', 6, '{\"status\": \"CONFIRMED\", \"total_amount\": 1000.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-24 19:53:25'),
(32, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-24 19:53:46'),
(33, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:53:46'),
(34, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:53:53'),
(35, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_IN\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:53:58'),
(36, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 19:54:01'),
(37, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 20:03:02'),
(38, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 20:07:40'),
(39, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 20:07:43'),
(40, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 20:07:48'),
(41, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 20:08:57'),
(42, 'reservations', 'UPDATE', 6, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 1000.00, \"payment_status\": \"PAID\"}', 1, '2026-02-24 20:13:49'),
(43, 'reservations', 'UPDATE', 16, '{\"status\": \"PENDING\", \"total_amount\": 450.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CONFIRMED\", \"total_amount\": 450.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-26 20:49:25'),
(44, 'reservations', 'UPDATE', 16, '{\"status\": \"CONFIRMED\", \"total_amount\": 450.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 450.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-26 20:49:27'),
(45, 'reservations', 'UPDATE', 16, '{\"status\": \"CHECKED_IN\", \"total_amount\": 450.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 450.00, \"payment_status\": \"PENDING\"}', 1, '2026-02-26 20:49:44'),
(46, 'reservations', 'UPDATE', 16, '{\"status\": \"CHECKED_IN\", \"total_amount\": 450.00, \"payment_status\": \"PENDING\"}', '{\"status\": \"CHECKED_IN\", \"total_amount\": 450.00, \"payment_status\": \"PAID\"}', 1, '2026-02-26 20:49:44'),
(47, 'reservations', 'UPDATE', 16, '{\"status\": \"CHECKED_IN\", \"total_amount\": 450.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 450.00, \"payment_status\": \"PAID\"}', 1, '2026-02-26 20:49:47'),
(48, 'reservations', 'UPDATE', 16, '{\"status\": \"CHECKED_OUT\", \"total_amount\": 450.00, \"payment_status\": \"PAID\"}', '{\"status\": \"CHECKED_OUT\", \"total_amount\": 450.00, \"payment_status\": \"PAID\"}', 1, '2026-02-26 20:49:51');

-- --------------------------------------------------------

--
-- Table structure for table `guests`
--

CREATE TABLE `guests` (
  `guest_id` int(11) NOT NULL,
  `guest_name` varchar(100) NOT NULL,
  `address` text DEFAULT NULL,
  `contact_number` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `identification_type` varchar(50) DEFAULT NULL,
  `identification_number` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `guests`
--

INSERT INTO `guests` (`guest_id`, `guest_name`, `address`, `contact_number`, `email`, `identification_type`, `identification_number`, `created_at`, `updated_at`) VALUES
(1, 'Nimal Perera', 'Nugegoda, Sri Lanka', '0771234567', 'nimal44@gmail.com', 'NIC', '942356002445', '2026-02-19 17:41:43', '2026-02-19 17:41:43');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL,
  `reservation_id` int(11) NOT NULL,
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `amount` decimal(10,2) NOT NULL,
  `payment_method` enum('CASH','CREDIT_CARD','DEBIT_CARD','ONLINE') NOT NULL,
  `transaction_reference` varchar(100) DEFAULT NULL,
  `processed_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`payment_id`, `reservation_id`, `payment_date`, `amount`, `payment_method`, `transaction_reference`, `processed_by`) VALUES
(1, 6, '2026-02-24 19:53:46', 1000.00, 'CREDIT_CARD', '85234123', 1),
(2, 16, '2026-02-26 20:49:44', 450.00, 'CREDIT_CARD', 'abcv123654', 1);

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `reservation_id` int(11) NOT NULL,
  `reservation_number` varchar(20) NOT NULL,
  `guest_id` int(11) NOT NULL,
  `room_type_id` int(11) NOT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `number_of_guests` int(11) DEFAULT 1,
  `status` enum('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') DEFAULT 'PENDING',
  `total_amount` decimal(10,2) DEFAULT NULL,
  `payment_status` enum('PENDING','PARTIAL','PAID','REFUNDED') DEFAULT 'PENDING',
  `special_requests` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`reservation_id`, `reservation_number`, `guest_id`, `room_type_id`, `check_in_date`, `check_out_date`, `number_of_guests`, `status`, `total_amount`, `payment_status`, `special_requests`, `created_by`, `created_at`, `updated_at`) VALUES
(1, 'RES-20260219-1000', 1, 1, '2026-02-20', '2026-02-22', 2, 'CANCELLED', 300.00, 'PENDING', 'Need an AC Room', 1, '2026-02-19 17:42:39', '2026-02-19 17:45:07'),
(2, 'RES-20260219-1001', 1, 3, '2026-02-23', '2026-02-27', 3, 'CONFIRMED', 1800.00, 'PENDING', 'none', 1, '2026-02-19 17:46:01', '2026-02-19 17:46:07'),
(3, 'RES-20260219-1002', 1, 1, '2026-02-19', '2026-02-21', 1, 'CHECKED_OUT', 300.00, 'PAID', 'none', 1, '2026-02-19 17:51:18', '2026-02-24 19:03:46'),
(4, 'RES-20260221-1000', 1, 2, '2026-02-21', '2026-02-22', 2, 'CHECKED_OUT', 250.00, 'PAID', '', 1, '2026-02-21 07:40:45', '2026-02-24 19:51:07'),
(5, 'RES-20260225-1000', 1, 2, '2026-02-26', '2026-03-01', 2, 'CANCELLED', 750.00, 'PENDING', '', 1, '2026-02-24 19:52:00', '2026-02-24 19:52:53'),
(6, 'RES-20260225-1001', 1, 2, '2026-02-25', '2026-03-01', 1, 'CHECKED_OUT', 1000.00, 'PAID', '', 1, '2026-02-24 19:53:13', '2026-02-24 19:53:58'),
(8, 'RES-20260225-5843', 1, 1, '2026-02-26', '2026-02-28', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-25 04:30:50', '2026-02-25 04:30:50'),
(9, 'RES-20260225-9014', 1, 1, '2026-02-26', '2026-02-28', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-25 04:31:29', '2026-02-25 04:31:29'),
(10, 'RES-20260225-1576', 1, 1, '2026-02-26', '2026-02-28', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-25 04:33:19', '2026-02-25 04:33:19'),
(11, 'RES-20260225-8911', 1, 1, '2026-02-26', '2026-02-28', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-25 04:33:53', '2026-02-25 04:33:53'),
(12, 'RES-20260225-4155', 1, 1, '2026-02-26', '2026-02-28', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-25 04:35:19', '2026-02-25 04:35:19'),
(13, 'RES-20260226-9686', 1, 1, '2026-02-27', '2026-03-01', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-26 14:50:10', '2026-02-26 14:50:10'),
(14, 'RES-20260226-3339', 1, 1, '2026-02-27', '2026-03-01', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-26 17:56:57', '2026-02-26 17:56:57'),
(15, 'RES-20260226-3063', 1, 1, '2026-02-27', '2026-03-01', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-02-26 18:04:17', '2026-02-26 18:04:17'),
(16, 'RES-20260227-7173', 1, 1, '2026-02-27', '2026-03-02', 1, 'CHECKED_OUT', 450.00, 'PAID', 'none', 1, '2026-02-26 20:48:44', '2026-02-26 20:49:47'),
(17, 'RES-20260301-2546', 1, 1, '2026-03-02', '2026-03-04', 2, 'PENDING', 300.00, 'PENDING', NULL, 1, '2026-03-01 12:40:53', '2026-03-01 12:40:53');

--
-- Triggers `reservations`
--
DELIMITER $$
CREATE TRIGGER `tr_reservation_update` AFTER UPDATE ON `reservations` FOR EACH ROW BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value, changed_by)
    VALUES (
        'reservations',
        'UPDATE',
        NEW.reservation_id,
        JSON_OBJECT(
            'status', OLD.status,
            'total_amount', OLD.total_amount,
            'payment_status', OLD.payment_status
        ),
        JSON_OBJECT(
            'status', NEW.status,
            'total_amount', NEW.total_amount,
            'payment_status', NEW.payment_status
        ),
        NEW.created_by
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `room_types`
--

CREATE TABLE `room_types` (
  `room_type_id` int(11) NOT NULL,
  `type_name` varchar(50) NOT NULL,
  `rate_per_night` decimal(10,2) NOT NULL,
  `capacity` int(11) NOT NULL,
  `description` text DEFAULT NULL,
  `amenities` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`amenities`)),
  `is_available` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `room_types`
--

INSERT INTO `room_types` (`room_type_id`, `type_name`, `rate_per_night`, `capacity`, `description`, `amenities`, `is_available`) VALUES
(1, 'Standard', 150.00, 2, 'Comfortable room with basic amenities', '[\"WiFi\", \"TV\", \"AC\"]', 1),
(2, 'Deluxe', 250.00, 3, 'Spacious room with ocean view', '[\"WiFi\", \"TV\", \"AC\", \"Mini Bar\", \"Balcony\"]', 1),
(3, 'Suite', 450.00, 4, 'Luxury suite with premium amenities', '[\"WiFi\", \"TV\", \"AC\", \"Mini Bar\", \"Balcony\", \"Jacuzzi\"]', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` enum('ADMIN','RECEPTIONIST','MANAGER') DEFAULT 'RECEPTIONIST',
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `role`, `is_active`, `created_at`, `last_login`) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'System Administrator', 'admin@oceanview.com', 'ADMIN', 1, '2026-02-19 16:08:11', '2026-03-01 16:23:22'),
(2, 'receptionist1', 'e10adc3949ba59abbe56e057f20f883e', 'John Doe', 'john@oceanview.com', 'RECEPTIONIST', 1, '2026-02-19 16:08:11', '2026-02-25 12:41:42');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `idx_table_record` (`table_name`,`record_id`);

--
-- Indexes for table `guests`
--
ALTER TABLE `guests`
  ADD PRIMARY KEY (`guest_id`),
  ADD KEY `idx_contact` (`contact_number`),
  ADD KEY `idx_email` (`email`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `processed_by` (`processed_by`),
  ADD KEY `idx_reservation` (`reservation_id`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservation_id`),
  ADD UNIQUE KEY `reservation_number` (`reservation_number`),
  ADD KEY `guest_id` (`guest_id`),
  ADD KEY `room_type_id` (`room_type_id`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `idx_reservation_number` (`reservation_number`),
  ADD KEY `idx_check_in` (`check_in_date`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `room_types`
--
ALTER TABLE `room_types`
  ADD PRIMARY KEY (`room_type_id`),
  ADD UNIQUE KEY `type_name` (`type_name`),
  ADD KEY `idx_type_name` (`type_name`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_log`
--
ALTER TABLE `audit_log`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT for table `guests`
--
ALTER TABLE `guests`
  MODIFY `guest_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `room_types`
--
ALTER TABLE `room_types`
  MODIFY `room_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=73;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`),
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`processed_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`guest_id`) REFERENCES `guests` (`guest_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`room_type_id`),
  ADD CONSTRAINT `reservations_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
