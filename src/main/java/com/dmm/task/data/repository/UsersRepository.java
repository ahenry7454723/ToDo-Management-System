package com.dmm.task.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

// Usersエンティティをインポート
import com.dmm.task.data.entity.Users;

/**
 * ユーザー情報にアクセスするためのリポジトリインターフェース。
 * JpaRepositoryを継承しているため、基本的なCRUD操作が自動的に提供されます。
 */
public interface UsersRepository extends JpaRepository<Users, String> {

    /**
     * ユーザー名を基にユーザー情報を検索するカスタムメソッド。
     * @param userName 検索対象のユーザー名
     * @return 検索されたユーザー情報（存在しない場合はnullを返す）
     */
    Users findByUserName(String userName);

}
