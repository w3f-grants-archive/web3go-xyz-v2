import { ApiProperty } from "@nestjs/swagger";
import { IsNotEmpty } from "class-validator";

export class RemoveTag4DashboardRequest {

    @ApiProperty({ required: true })
    @IsNotEmpty()
    dashboardId: number;

    @ApiProperty({ required: true })
    @IsNotEmpty()
    tagIds: number[];

}