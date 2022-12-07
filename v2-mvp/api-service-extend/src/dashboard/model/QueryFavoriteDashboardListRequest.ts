import { ApiProperty } from "@nestjs/swagger";
import { IsNotEmpty } from "class-validator";
import { PageRequest } from "src/viewModel/base/pageRequest";

export class QueryFavoriteDashboardListRequest extends PageRequest {
    @ApiProperty({
        required: true,
    })
    @IsNotEmpty()
    accountId: string;
}