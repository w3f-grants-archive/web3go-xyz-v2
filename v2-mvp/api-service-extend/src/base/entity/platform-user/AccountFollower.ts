import { ApiProperty } from '@nestjs/swagger';
import { Entity, Column, PrimaryColumn, PrimaryGeneratedColumn } from 'typeorm';

@Entity("account_follower", { schema: "public" })
export class AccountFollower {

    @PrimaryColumn({
        type: "text",
        name: "account_id",
    })
    accountId: string;


    @PrimaryColumn({
        type: "text",
        name: "followed_account_id",
    })
    followedAccountId: string;

    @Column("timestamp with time zone", {
        name: "created_at"
    })
    createdAt: Date;




}