local length = tableLength(args)
if length >= 3 then
    local data = {}
    for i = 3, length, 1 do
        data[#data+1] = args[i]
    end
    if not lunan.sendPacket(args[1], args[2], data) then
        print(args[1] .. ": no such address")
    end
else
    print("usage: send [DESTINATION] [PROTOCOL] [DATA...]")
end
