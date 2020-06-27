module controller(clear, load, reset, clock);

output reg[0:0] clear;
output reg[0:0] load;

input wire reset;
input wire clock;

reg[0:0] current_state;
reg[0:0] next_state;

parameter S0 = 1'b0,
		  S1 = 1'b1;

initial begin
	current_state <= S0;
end

always @(reset, current_state) begin
	case(current_state)
		S0: begin
			if(reset == 0) begin
				next_state <= S1;
			end
			
			else begin
				next_state <= S0;
			end
		end
		
		S1: begin
			if(reset == 0) begin
				next_state <= S1;
			end
			
			else begin
				next_state <= S0;
			end
		end
	endcase
end

always @(reset, current_state) begin
	case(current_state)
		S0: begin
			clear <= 1'b1;
			load <= 1'b0;
		end
		S1: begin
			clear <= 1'b0;
			load <= 1'b1;
		end
	endcase
end	
		
always @(posedge clock) begin
    current_state <= next_state; 
end

endmodule