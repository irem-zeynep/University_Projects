module datapath(min, max, data, clear, load, clock);

output reg signed[0:31] min;
output reg signed[0:31] max;

input wire signed[0:31] data;

input wire clear;
input wire load;
input wire clock;

always @(posedge clock) begin
	if (clear == 0 && load == 1) begin
		if (data > max) max <= data;	
		if (data < min) min <= data;
	end
	
	if(clear == 1) begin
		max <= -32'd2147483648;
		min <= 32'd2147483647;	
	end
end

endmodule