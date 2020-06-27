`timescale 1ns / 1ns

module source(y, x, rst, clk);

output reg [1:0]y;
input wire x;
input wire rst;
input wire clk;

parameter S00 = 2'b00,
	S01 = 2'b01,
	S10 = 2'b10;
	
output reg [1:0] currentState;
output reg [1:0] nextState;

initial begin
	currentState <= S00;
end

always@(x, currentState) begin
	case(currentState)
		S00: begin
			if(x == 0) begin
				nextState <= S00;
				y <= 2'b01;
			end
			else begin
				nextState <= S10;
				y <= 2'b00;
			end
		end
		S01: begin
			if(x == 0) begin
				nextState <= S00;
				y <= 2'b00;
			end
			else begin
				nextState <= S01;
				y <= 2'b01;
			end
		end
		S10: begin
			if(x == 0) begin
				nextState <= S01;
				y <= 2'b10;
			end
			else begin
				nextState <= S00;
				y <= 2'b00;
			end
		end
	endcase
end
		
always@(posedge clk) begin
	if(rst) begin
		currentState <= S00;
	end
	else begin
		currentState <= nextState;
	end
end

endmodule
	