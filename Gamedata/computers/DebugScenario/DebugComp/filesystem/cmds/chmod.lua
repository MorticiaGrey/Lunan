-- For this command I basically just implemented it in java because I really really
-- did not want to do advanced text processing in lua
local input = "chmod"
for i = 1, tableLength(args), 1 do
    input = input .. " " .. args[i]
end
for i = 1, tableLength(flags), 1 do
    input = input .. " " .. flags[i]
end
lunan.chmod(input)
